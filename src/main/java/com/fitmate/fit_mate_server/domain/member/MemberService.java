package com.fitmate.fit_mate_server.domain.member;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fitmate.fit_mate_server.global.upload.ImageUploadService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final ImageUploadService imageUploadService;

    // 비밀번호 정책: 영문 + 숫자 + 특수문자 모두 포함, 8~20자
    // MemberJoinRequest의 @Pattern과 동일한 규칙 (서비스 레벨 이중 방어)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$"
    );
    
    /** 토큰에서 추출한 memberId로 내 정보 조회 */
    public MemberResponse getMe(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    
        return toResponse(member);
    }

    /** 이름, 자기소개, 키 수정 (null인 필드는 변경하지 않음) */
    public MemberResponse updateProfile(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updateProfile(request.getName(), request.getIntroduction(), request.getHeight());
        return toResponse(member);
    }

    /** 체중/근육량/체지방률 공개 범위 수정 (null인 필드는 변경하지 않음) */
    public MemberResponse updatePrivacySettings(Long memberId, MemberPrivacyUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updatePrivacySettings(request.getWeightPrivacy(), request.getMusclePrivacy(), request.getFatPrivacy());
        return toResponse(member);
    }

    /** 비밀번호 변경: 현재 비밀번호 확인 후 새 비밀번호로 교체 */
    public void changePassword(Long memberId, MemberPasswordUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        validatePassword(request.getNewPassword());
        validatePasswordMatch(request.getNewPassword(), request.getCheckNewPassword());

        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /** 프로필 사진 업로드 및 교체 (기존 사진은 Cloudinary에서 삭제) */
    public MemberResponse updateProfileImage(Long memberId, MultipartFile image) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String oldPublicId = member.getProfileImagePublicId();

        ImageUploadService.UploadResult result = imageUploadService.upload(image, "fitmate/profile");
        member.updateProfileImage(result.url(), result.publicId());

        if (oldPublicId != null) {
            imageUploadService.delete(oldPublicId);
        }

        return toResponse(member);
    }

    /** 회원 탈퇴: 비밀번호 확인 후 프로필 사진은 즉시 삭제, 나머지 개인정보는 유예기간 후 배치로 익명화됨 */
    public void withdraw(Long memberId, MemberWithdrawRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (member.getProfileImagePublicId() != null) {
            imageUploadService.delete(member.getProfileImagePublicId());
        }

        member.withdraw();
    }

    private MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .introduction(member.getIntroduction())
            .profileImageUrl(member.getProfileImageUrl())
            .height(member.getHeight())
            .weightPrivacy(member.getWeightPrivacy())
            .musclePrivacy(member.getMusclePrivacy())
            .fatPrivacy(member.getFatPrivacy())
            .build();
    }

    public Long join(MemberJoinRequest request){
        // 1. 생년월일 검증 (서버 시간 기준 미래 불가)
        validateBirthDate(request.getBirthDate());

        // 2. 비밀번호 규칙 검증
        validatePassword(request.getPassword());

        // 3. 비밀번호 확인 일치 검증
        validatePasswordMatch(request.getPassword(), request.getCheckPassword());

        // 4. 중복 이메일 검증 
        memberRepository.findByEmail(request.getEmail()).ifPresent(m ->{
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        });

        // 5. 비밀번호 해시 처리 후 저장
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // 2. 저장
        Member newMember = Member.builder()
            .email(request.getEmail())
            .password(encodePassword) 
            .name(request.getName())
            .birthDate(request.getBirthDate())
            .height(request.getHeight())
            .build();

        // 3. 저장 및 ID 반환 
        Member savedMember = memberRepository.save(newMember);
        return savedMember.getId();
    }

    /**
     * 생년월일이 null이 아니고, 오늘(서버 시간) 이후 날짜면 예외 발생.
     * DTO의 @PastOrPresent로 컨트롤러 단에서 걸러지더라도,
     * 서비스가 다른 경로로 호출될 경우를 대비한 이중 방어.
     */
    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("생년월일은 현재 날짜보다 미래일 수 없습니다.");
        }
    }

    
    // 비밀번호가 정책(영문+숫자+특수문자 포함, 8~20자)을 만족하는지 검증.
    
    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자여야 합니다.");
        }
    }

    // 비밀번호와 비밀번호 확인이 일치하는지 검증.
    private void validatePasswordMatch(String password, String checkPassword) {
        if (password == null || !password.equals(checkPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}

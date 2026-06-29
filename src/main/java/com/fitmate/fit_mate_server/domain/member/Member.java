package com.fitmate.fit_mate_server.domain.member;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    // 각가의 지표별 공개 설정 옵션 (Enum 매핑)
    
    @Column(columnDefinition = "TINYINT DEFAULT 1") // DB 공간 효율을 위해 TINYINT 사용 (생략 가능)
    private PrivacyOption weightPrivacy = PrivacyOption.PRIVATE; // 기본값 : 나만보기, DB에는 PRIVATE=0, DELTA_ONLY=1, PUBLIC=2 로 저장됨

    
    @Column(columnDefinition = "TINYINT")
    private PrivacyOption musclePrivacy = PrivacyOption.PRIVATE;


    @Column(columnDefinition = "TINYINT")
    private PrivacyOption fatPrivacy = PrivacyOption.PRIVATE;

    @Builder
    public Member(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }
}

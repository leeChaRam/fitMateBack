package com.fitmate.fit_mate_server.domain.body;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitmate.fit_mate_server.domain.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 회원과 연결
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    private LocalDate measureDate; // 측정일 (날짜만)
    private Double weight;        // 체중
    private Double height;        // 키
    private Double muscleMass;    // 근육량
    private Double fatMass;       // 체지방량
    
    
    @Column(columnDefinition = "TEXT")
    private String memo;          // 메모 (길어질 수 있으니 TEXT)
    private LocalDateTime createdAt; // 기록 생성 시간


    // 데이터가 처음 INSERT 될 때 현재 시간을 자동으로 주입해 줌
    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}

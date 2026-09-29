package com.fitmate.fit_mate_server.domain.mate.feed;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitmate.fit_mate_server.domain.mate.Mate;
import com.fitmate.fit_mate_server.domain.member.Member;

public interface MateReactionRepository extends JpaRepository<MateReaction, Long> {
    Optional<MateReaction> findByMateAndPostTypeAndPostIdAndMember(Mate mate, FeedPostType postType, Long postId, Member member);
    List<MateReaction> findByMateAndPostTypeAndPostIdIn(Mate mate, FeedPostType postType, List<Long> postIds); // 피드 목록용 일괄 조회
}

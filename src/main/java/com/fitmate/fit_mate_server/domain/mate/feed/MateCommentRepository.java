package com.fitmate.fit_mate_server.domain.mate.feed;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitmate.fit_mate_server.domain.mate.Mate;

public interface MateCommentRepository extends JpaRepository<MateComment, Long> {
    List<MateComment> findByMateAndPostTypeAndPostIdOrderByCreatedAtAsc(Mate mate, FeedPostType postType, Long postId); // 댓글 상세 목록
    List<MateComment> findByMateAndPostTypeAndPostIdIn(Mate mate, FeedPostType postType, List<Long> postIds); // 피드 목록용 일괄 조회 (댓글 수)
}

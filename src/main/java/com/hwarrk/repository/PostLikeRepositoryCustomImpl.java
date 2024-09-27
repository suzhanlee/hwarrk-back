package com.hwarrk.repository;

import com.hwarrk.common.SliceCustomImpl;
import com.hwarrk.common.util.PageUtil;
import com.hwarrk.entity.Post;
import com.hwarrk.entity.PostLike;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hwarrk.entity.QPost.post;
import static com.hwarrk.entity.QPostLike.postLike;
import static com.hwarrk.entity.QProject.project;
import static com.hwarrk.entity.QRecruitingPosition.recruitingPosition;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SliceCustomImpl getLikedPostSlice(Long memberId, Long lastPostLikeId, Pageable pageable) {
        List<PostLike> postLikes = getPostLikes(memberId, lastPostLikeId, pageable);

        boolean hasNext = PageUtil.hasNextPage(postLikes, pageable);

        List<Post> likedPosts = postLikes.stream()
                .map(PostLike::getPost)
                .toList();

        return new SliceCustomImpl(likedPosts, pageable, hasNext, PageUtil.getLastElement(postLikes).getId());
    }

    private List<PostLike> getPostLikes(Long memberId, Long lastPostLikeId, Pageable pageable) {
        return jpaQueryFactory
                .select(postLike)
                .from(postLike)
                .join(postLike.post, post).fetchJoin()
                .leftJoin(post.positions, recruitingPosition).fetchJoin()
                .leftJoin(post.project, project).fetchJoin()
                .where(
                        ltPostLikeId(lastPostLikeId),
                        eqMemberId(memberId)
                )
                .orderBy(postLike.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return postLike.member.id.eq(memberId);
    }

    private BooleanExpression ltPostLikeId(Long lastPostLikeId) {
        return lastPostLikeId == null ? null : postLike.id.lt(lastPostLikeId);
    }
}
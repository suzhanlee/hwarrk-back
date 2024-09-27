package com.hwarrk.controller;

import com.hwarrk.common.apiPayload.CustomApiResponse;
import com.hwarrk.common.constant.LikeType;
import com.hwarrk.common.dto.res.SliceRes;
import com.hwarrk.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post-likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(summary = "구인글 찜하기",
            responses = {
                    @ApiResponse(responseCode = "COMMON200", description = "찜하기 성공"),
                    @ApiResponse(responseCode = "POST_LIKE4091", description = "찜이 이미 존재합니다"),
                    @ApiResponse(responseCode = "POST_LIKE4041", description = "찜을 찾을 수 없습니다")
            }
    )
    @PostMapping("/posts/{postId}")
    public CustomApiResponse likeProject(@AuthenticationPrincipal Long loginId,
                                         @PathVariable("postId") Long postId,
                                         @RequestParam LikeType likeType) {
        postLikeService.likePost(loginId, postId, likeType);
        return CustomApiResponse.onSuccess();
    }

    @Operation(summary = "구인글 찜목록 조회")
    @GetMapping
    public CustomApiResponse getLikedPosts(@AuthenticationPrincipal Long loginId,
                                           @RequestParam Long lastPostLikeId,
                                           @PageableDefault Pageable pageable) {
        SliceRes res = postLikeService.getLikedPostSlice(loginId, lastPostLikeId, pageable);
        return CustomApiResponse.onSuccess(res);
    }
}
package com.hwarrk.service;

import com.hwarrk.common.dto.res.OauthLoginRes;
import com.hwarrk.entity.Member;
import com.hwarrk.jwt.TokenProvider;
import com.hwarrk.oauth2.member.OauthMember;
import com.hwarrk.oauth2.param.OauthParams;
import com.hwarrk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OauthServiceImpl implements OauthService {
    private final RequestOauthInfoService requestOauthInfoService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public OauthLoginRes getMemberByOauthLogin(OauthParams oauthParams) {
        log.debug("------- Oauth 로그인 시도 -------");

        OauthMember request = requestOauthInfoService.request(oauthParams);
        Optional<Member> byOauthProviderAndSocialId = memberRepository.findByOauthProviderAndSocialId(request.getOauthProvider(), request.getSocialId());

        // 기존 유저
        if (byOauthProviderAndSocialId.isPresent()) {
            return new OauthLoginRes(
                    byOauthProviderAndSocialId.get().getId(),
                    byOauthProviderAndSocialId.get().getRole(),
                    tokenProvider.issueAccessToken(byOauthProviderAndSocialId.get().getId()),
                    tokenProvider.issueRefreshToken(byOauthProviderAndSocialId.get().getId()));
        }

        //신규 유저 DB에 저장
        Member savedMember = memberRepository.save(new Member(request));
        return new OauthLoginRes(savedMember.getId(), savedMember.getRole(), null, null);
    }
}
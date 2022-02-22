package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.Repository.UserRepository;
import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private UserRepository userRepository;
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
	// 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		System.out.println("getClientRegistration: " +userRequest.getClientRegistration());
		System.out.println("getAccessToken: " +userRequest.getAccessToken().getTokenValue());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
		// userRequest 정보 -> loadUser함수 호출 -> 구글로부터 회원프로필 받음.
		System.out.println("getAttributes: " +super.loadUser(userRequest).getAttributes());
		
		// 순환 참조때문에 넣은 인코더
		BCryptPasswordEncoder myEncoder = new BCryptPasswordEncoder();
		
		String provider = userRequest.getClientRegistration().getRegistrationId(); // google
		String providerId = oauth2User.getAttribute("sub");
		String username = provider+"_"+providerId;
		String password = myEncoder.encode("겟인데어");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			System.out.println("로그인이 최초입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
		} else {
			System.out.println("로그인을 이미 한 적이 있습니다.");
		}
		
		// 회원가입 강제로 진행
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}

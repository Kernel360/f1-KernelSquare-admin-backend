package com.kernelsquare.alertapi.domain.alert.controller;

import static com.kernelsquare.core.common_response.response.code.AlertResponseCode.*;
import static com.kernelsquare.alertapi.config.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.kernelsquare.core.type.AuthorityType;
import com.kernelsquare.domainmongodb.domain.alert.entity.Alert;
import com.kernelsquare.domainmysql.domain.authority.entity.Authority;
import com.kernelsquare.domainmysql.domain.member.entity.Member;
import com.kernelsquare.domainmysql.domain.member_authority.entity.MemberAuthority;
import com.kernelsquare.alertapi.config.RestDocsControllerTest;
import com.kernelsquare.alertapi.domain.alert.dto.AlertDto;
import com.kernelsquare.alertapi.domain.alert.facade.AlertFacade;
import com.kernelsquare.alertapi.domain.alert.manager.SseManager;
import com.kernelsquare.alertapi.domain.auth.dto.MemberAdapter;
import com.kernelsquare.alertapi.domain.auth.dto.MemberAdaptorInstance;

@DisplayName("알림 컨트롤러 테스트")
@WebMvcTest(AlertController.class)
class AlertControllerTest extends RestDocsControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private SseManager sseManager;
	@MockBean
	private AlertFacade alertFacade;

	@Test
	@DisplayName("SSE 구독 성공 시 200 OK를 반환한다.")
	@WithMockUser
	void testSubscribe() throws Exception {
		Member member = Member.builder()
			.id(1L)
			.nickname("machine")
			.email("awdag@nsavasc.om")
			.password("hashed")
			.experience(1200L)
			.introduction("basfas")
			.authorities(List.of(
				MemberAuthority.builder()
					.member(Member.builder().build())
					.authority(Authority.builder().authorityType(AuthorityType.ROLE_USER).build())
					.build()))
			.imageUrl("agawsc")
			.build();

		MemberAdapter memberAdapter = new MemberAdapter(MemberAdaptorInstance.of(member));

		SseEmitter sseEmitter = new SseEmitter(60 * 1000L);

		doReturn(sseEmitter).when(sseManager).subscribe(memberAdapter);

		//when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/api/v1/alerts/sse")
				.with(user(memberAdapter))
				.contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
				.accept(MediaType.TEXT_EVENT_STREAM_VALUE)
				.characterEncoding("UTF-8"));

		//then
		resultActions.andExpect(status().isOk())
			.andDo(document("alert-subscribe"));

		//verify
		verify(sseManager, times(1)).subscribe(any(MemberAdapter.class));
		verifyNoMoreInteractions(sseManager);
	}

	@Test
	@DisplayName("알림 조회 성공시 200 OK와 정상 응답을 반환한다.")
	@WithMockUser
	void testFindAllAlerts() throws Exception {
		//given
		Member member = Member.builder()
			.id(1L)
			.nickname("machine")
			.email("awdag@nsavasc.om")
			.password("hashed")
			.experience(1200L)
			.introduction("basfas")
			.authorities(List.of(
				MemberAuthority.builder()
					.member(Member.builder().build())
					.authority(Authority.builder().authorityType(AuthorityType.ROLE_USER).build())
					.build()))
			.imageUrl("agawsc")
			.build();

		MemberAdapter memberAdapter = new MemberAdapter(MemberAdaptorInstance.of(member));

		AlertDto.MessageResponse findAllResponse = AlertDto.MessageResponse.builder()
			.id("alt_y8fWs19jzJFhZwi4")
			.alertType(Alert.AlertType.QUESTION_REPLY)
			.recipient("시나모롤")
			.sendTime(LocalDateTime.now())
			.payload(Map.of(
				"questionTitle", "관리자의 미덕",
				"sender", "고김홍",
				"questionId", "10"
			))
			.build();

		List<AlertDto.MessageResponse> findAllResponseList = List.of(findAllResponse);

		AlertDto.PersonalFindAllResponse response = AlertDto.PersonalFindAllResponse.from(findAllResponseList);

		doReturn(response).when(alertFacade).findAllAlerts(any(MemberAdapter.class));

		//when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/api/v1/alerts")
				.with(user(memberAdapter))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8"));

		//then
		resultActions.andExpect(status().is(MY_ALERT_ALL_FOUND.getStatus().value()))
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andDo(document("alert-all-found", getDocumentResponse(),
				responseFields(
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
					fieldWithPath("msg").type(JsonFieldType.STRING).description("응답 메시지"),
					fieldWithPath("data.personal_alert_list[].id").type(JsonFieldType.STRING)
						.description("알림 수신자 ID"),
					fieldWithPath("data.personal_alert_list[].alert_type").type(JsonFieldType.STRING)
						.description("알림 타입"),
					fieldWithPath("data.personal_alert_list[].recipient").type(JsonFieldType.STRING)
						.description("알림 수신자 닉네임"),
					fieldWithPath("data.personal_alert_list[].send_time").type(JsonFieldType.STRING)
						.description("알림 보낸 시간"),
					fieldWithPath("data.personal_alert_list[].payload.questionTitle").type(JsonFieldType.STRING)
						.description("질문 답변 알림 - 질문 제목"),
					fieldWithPath("data.personal_alert_list[].payload.sender").type(JsonFieldType.STRING)
						.description("질문 답변 알림 - 답변 작성자"),
					fieldWithPath("data.personal_alert_list[].payload.questionId").type(JsonFieldType.STRING)
						.description("질문 답변 알림 - 질문 ID")
				)));

		//verify
		verify(alertFacade, times(1)).findAllAlerts(any(MemberAdapter.class));
		verifyNoMoreInteractions(alertFacade);
	}
}
package com.hongsi.martholidayalarm.bot.kakao.service;

import com.hongsi.martholidayalarm.bot.kakao.domain.BotResponse;
import com.hongsi.martholidayalarm.bot.kakao.domain.Button;
import com.hongsi.martholidayalarm.bot.kakao.domain.Keyboard;
import com.hongsi.martholidayalarm.bot.kakao.domain.Message;
import com.hongsi.martholidayalarm.bot.kakao.domain.UserRequest;
import com.hongsi.martholidayalarm.bot.kakao.repository.KakaoBotRepository;
import com.hongsi.martholidayalarm.mart.domain.Mart;
import com.hongsi.martholidayalarm.mart.domain.MartType;
import com.hongsi.martholidayalarm.mart.service.MartService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class KakaoBotService {

	private final MartService martService;

	private final KakaoBotRepository kakaoBotRepository;

	public BotResponse parse(UserRequest userRequest) throws Exception {
		UserRequest beforeRequest = kakaoBotRepository.findByUserKey(userRequest.getUserKey());
		if (userRequest.isSame(beforeRequest)) {
			throw new IllegalStateException("중복된 요청입니다.");
		}
		userRequest.readyToUpdate(beforeRequest);
		kakaoBotRepository.save(userRequest);
		return new BotResponse(getMessageForResponse(userRequest),
				getKeyboardForResponse(userRequest));
	}

	private Message getMessageForResponse(UserRequest userRequest) throws NoSuchFieldException {
		Button selectedButton = userRequest.getButton();
		if (selectedButton == Button.BRANCH) {
			MartType martType = MartType
					.of(userRequest.getBeforeRequest(Button.MARTTYPE));
			String branchName = userRequest.getBeforeRequest(Button.BRANCH);
			Mart mart = martService.getMart(martType, branchName);
			return new Message(mart);
		}
		return new Message(selectedButton.getMessage());
	}

	private Keyboard getKeyboardForResponse(UserRequest userRequest) {
		return new Keyboard(getButtons(userRequest));
	}

	private List<String> getButtons(UserRequest userRequest) {
		try {
			switch (userRequest.getButton()) {
				case DEFAULT:
					return martService.getMartTypes().stream()
							.map(martType -> martType.getName())
							.collect(Collectors.toList());
				case MARTTYPE:
					MartType martType = MartType.of(userRequest.getBeforeRequest(Button.MARTTYPE));
					return martService.getRegions(martType);
				case REGION:
					martType = MartType.of(userRequest.getBeforeRequest(Button.MARTTYPE));
					String region = userRequest.getBeforeRequest(Button.REGION);
					return martService.getBranches(martType, region);
			}
		} catch (NoSuchFieldException e) {
			log.error("Can not create response : " + e.getMessage());
		}
		return Keyboard.getDefaultKeyboardToList();
	}

	public BotResponse reset(String userKey) {
		deleteUserRequest(userKey);
		Message wrongMessage = new Message("잘못된 요청입니다 다시 선택해주세요");
		Keyboard defaultKeyboard = new Keyboard(Keyboard.DEFAULT_KEYBOARD);
		return new BotResponse(wrongMessage, defaultKeyboard);
	}

	public void deleteUserRequest(String userKey) {
		kakaoBotRepository.deleteByUserKey(userKey);
	}
}

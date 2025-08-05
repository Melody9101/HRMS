package com.example.Human_Resource_Management_System_HRMS_.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.vo.SseEmitterRes;

@CrossOrigin
@RestController
@RequestMapping("/sse")
public class SseController {

	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	/**
	 * 訂閱排程釋出資料用 API <br>
	 * 注意: 如果排程釋出的當下前端沒有接到此 API 會造成資料遺失<br>
	 * 有另外開一些 API 來獲取排程釋出資料紀錄<br>
	 * 此 API 不需要一定要接<br>
	 * 此 API 路徑: http://localhost:8080/subscribe
	 * 
	 * @return
	 */
	@GetMapping("/subscribe")
	public SseEmitterRes subscribe() {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 永遠保持連線
		emitters.add(emitter);

		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError(e -> emitters.remove(emitter));

		return new SseEmitterRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), emitter);
	}

	public void sendToClients(Object data) {
		List<SseEmitter> deadEmitters = new ArrayList<>();
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("quizUpdate").data(data));
			} catch (Exception e) {
				deadEmitters.add(emitter);
			}
		}
		emitters.removeAll(deadEmitters);
	}
}

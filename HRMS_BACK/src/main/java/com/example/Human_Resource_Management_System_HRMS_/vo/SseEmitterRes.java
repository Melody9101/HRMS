package com.example.Human_Resource_Management_System_HRMS_.vo;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseEmitterRes extends BasicRes {

	private SseEmitter emitter;

	public SseEmitter getEmitter() {
		return emitter;
	}

	public void setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
	}

	public SseEmitterRes() {
		super();
	}

	public SseEmitterRes(int code, String message) {
		super(code, message);
	}

	public SseEmitterRes(int code, String message, SseEmitter emitter) {
		super(code, message);
		this.emitter = emitter;
	}

}

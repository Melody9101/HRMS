package com.example.Human_Resource_Management_System_HRMS_.aspect;

import java.net.InetAddress;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @Component: 將此 class 託管成元件類(由於無法明確的將要託管的類別歸類為哪種類型時用 @Component，<br>
 *             例如 service 可以明確歸類成 @Service)<br>
 * 
 * @Aspect: 讓此 class 可以使用切面的功能
 */
@Component
@Aspect
public class LoginAspect {

	// 各個符號的意思請參考投影片
	@Pointcut("execution (public * com.example.Human_Resource_Management_System_HRMS_.controller.*.*(..)) "//
			+ " && !execution (public * com.example.Human_Resource_Management_System_HRMS_.controller.*.login(..)) "
			+ " && !execution (public * com.example.Human_Resource_Management_System_HRMS_.controller.*.checkLogin(..)) "
			+ " && !execution (public * com.example.Human_Resource_Management_System_HRMS_.controller.*.updatePwdByEmail(..)) "
			+ " && !execution (public * com.example.Human_Resource_Management_System_HRMS_.controller.*.sendVerificationLetter(..)) "
			+ " && !execution (public * com.example.Human_Resource_Management_System_HRMS_.controller.*.checkVerification(..)) ")
	public void pointcut() {

	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		// 要轉型為 ServletRequestAttributes；
		// 因為 RequestContextHolder.getRequestAttributes() 的型別是 RequestAttributes；
		// 但 RequestAttributes 沒有 getRequest() 此方法可以呼叫
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		// 從 ServletRequestAttributes 取得 httpRequest
		HttpServletRequest httpRequest = attributes.getRequest();
		// 從 ServletRequestAttributes 取得 HttpSession
		HttpSession httpSession = httpRequest.getSession();

		Class<?> returnType = ((MethodSignature) pjp.getSignature()).getMethod().getReturnType();

		Object res = loginCheck(httpSession, returnType);
		if (res != null) {
			return res;
		}

		// 取得客戶端的 ip 位址
		String ipAddr = getIpAddr(httpRequest);
		// 取得伺服器 ip 位址
		String localIp = InetAddress.getLocalHost().getHostAddress();
		// 比對雙方 ip 位址
		String ipPrefix = getIpPrefix(ipAddr, 2);
		String localIpPrefix = getIpPrefix(localIp, 2);

		if (!ipPrefix.equals(localIpPrefix)) {
			return new BasicRes(ResMessage.ACCESS_DENIED.getCode(), //
					ResMessage.ACCESS_DENIED.getMessage());
		}

		// pjp.proceed(): 讓原本的程式繼續往下執行
		// 將原本的執行結果接回來後再回傳出去
		return pjp.proceed();
	}

	private Object loginCheck(HttpSession session, Class<?> returnType) {
		// 確認是否已有先 login 成功
		// getAttribute 取得值的型態是 Object ，要強制轉型成 String
		Integer sessionAccount = (Integer) session.getAttribute("accountId");
		String sessionId = (String) session.getAttribute("sessionId");

		// 比對暫存在 session 中的值和 req 裡面的帳密是否一樣
		// sessionAccount 或 sessionId 是 null 表示尚未登入成功過
		if (sessionAccount == null || sessionId == null) {
			return buildErrorResponse(returnType, ResMessage.PLEASE_LOGIN_FIRST.getCode(),
					ResMessage.PLEASE_LOGIN_FIRST.getMessage());
		}

		return null;
	}

	private Object buildErrorResponse(Class<?> returnType, int code, String msg) {
		try {
			// 嘗試找一個帶 (int, String) 的建構子
			return returnType.getConstructor(int.class, String.class).newInstance(code, msg);
		} catch (Exception e) {
			// 如果沒找到，就退回最簡單的 BasicRes
			return new BasicRes(code, msg);
		}
	}

	/**
	 * 取得客戶端主機 ip 位址
	 *
	 * @param request
	 * @return
	 */
	private String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
				// 根據網卡取得本機配置的 IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		// 多個代理的情况，第一個 IP 為客戶端真實 IP,多個 IP 按照','分割
		if (ip != null && ip.length() > 15) {
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}

	private String getIpPrefix(String ip, int parts) {
		if (ip == null)
			return "";
		String[] segments = ip.split("\\.");
		if (segments.length < parts)
			return "";
		return String.join(".", java.util.Arrays.copyOf(segments, parts));
	}
}

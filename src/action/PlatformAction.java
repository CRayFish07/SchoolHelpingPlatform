package action;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import util.StringUtil;

import com.google.gson.Gson;

import bean.TransferOnlineUserBasicInfo;
import bean.TransferPlatformStatisticsInfo;
import bean.TransferResultInfo;
import bean.TransferUserInfo;
import bean.TransferUserUnreadInfo;
import cache.Configurations;
import cache.PlatformOnlineUserStorage;
import cache.PlatformStatistics;
import cache.PlatformUnreadChatInfoStorage;
import cache.ResultCodeStorage;
import dao.UserService;
import exception.IllegalParameterException;
import exception.NoLoginException;

public class PlatformAction extends BaseAction implements ServletResponseAware, ServletRequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3093345867957179721L;
	
	private static final int statistic = 0;
	private static final int user = 1;
	private static final int unread = 2;
	
	private HttpServletResponse response;
	private HttpServletRequest request;
	
	private String toUid;
	
	private String opt;
	private String ui;

	public String getToUid() {
		return toUid;
	}

	public void setToUid(String toUid) {
		this.toUid = toUid;
	}

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void statistic() {
		operations(statistic);
	}
	
	public void user() {
		operations(user);
	}
	
	public void unread() {
		operations(unread);
	}
	
	private void operations(int op) {
		try {
			if (ui == null || "".equals(ui))
				ui = Configurations.action_general_UI_USER;
			switch(ui) {
			case Configurations.action_general_UI_ADMIN:
				checkAdminLogin();
				break;
			case Configurations.action_general_UI_USER:
			default:
				checkUserLogin();
			}
			switch (op) {
			case statistic: {
				TransferPlatformStatisticsInfo info = new TransferPlatformStatisticsInfo();
				info.setLogin(PlatformOnlineUserStorage.getOnlineUserCount());
				info.setTodaylogin(PlatformStatistics.getTodayLoginCount());
				info.setOnline(PlatformStatistics.getOnlineCount());
				info.setTodayproblem(PlatformStatistics.getTodayProblemCount());
				info.setTodaysolve(PlatformStatistics.getTodaySolvedCount());
				TransferResultInfo<TransferPlatformStatisticsInfo> rs = new TransferResultInfo<TransferPlatformStatisticsInfo>();
				rs.setMsgType(ResultCodeStorage.type_success);
				rs.setMsgCode(ResultCodeStorage.code_success);
				rs.setMsgContent(info);
				sendMsgtoWeb(rs);
			}
			break;
			case user: {
				TransferOnlineUserBasicInfo user = (TransferOnlineUserBasicInfo) request.getSession().getAttribute(Configurations.session_online_user_key);
				TransferResultInfo<TransferOnlineUserBasicInfo> rs = new TransferResultInfo<TransferOnlineUserBasicInfo>();
				rs.setMsgType(ResultCodeStorage.type_success);
				rs.setMsgCode(ResultCodeStorage.code_success);
				rs.setMsgContent(user);
				sendMsgtoWeb(rs);
			}
			break;
			case unread: {
				if (opt == null || "".equals(opt))
					opt = Configurations.action_platform_opt_get;
				TransferOnlineUserBasicInfo user = (TransferOnlineUserBasicInfo) request.getSession().getAttribute(Configurations.session_online_user_key);
				switch(opt) {
				case Configurations.action_platform_opt_clear: {
					if (toUid == null || "".equals(toUid))
						throw new IllegalParameterException("toUid 参数不能为空");
					PlatformUnreadChatInfoStorage.removeUnreadChatInfoCount(user.getUser().getUid(), toUid);
					TransferResultInfo<List<TransferUserUnreadInfo>> rs = new TransferResultInfo<List<TransferUserUnreadInfo>>();
					rs.setMsgType(ResultCodeStorage.type_success);
					rs.setMsgCode(ResultCodeStorage.code_success);
					sendMsgtoWeb(rs);
				}
				break;
				case Configurations.action_platform_opt_get:
				default: {
					List<TransferUserUnreadInfo> list_unread = new ArrayList<TransferUserUnreadInfo>();
					Map<String, Integer> map_unread = PlatformUnreadChatInfoStorage.getUnreadChatInfo(user.getUser().getUid());
					if (map_unread != null && map_unread.size() > 0) {
						for(String uid : map_unread.keySet()) {
							TransferUserUnreadInfo unreadInfo = new TransferUserUnreadInfo();
							unreadInfo.setUserUid(uid);
							TransferOnlineUserBasicInfo unreadUser = 
									PlatformOnlineUserStorage.getOnlineUserHttpSession(uid) == null ? 
											null
											: 
											(TransferOnlineUserBasicInfo) PlatformOnlineUserStorage.getOnlineUserHttpSession(uid).getAttribute(Configurations.session_online_user_key);
							if (unreadUser != null && unreadUser.getUser() != null) {
								unreadInfo.setUserName(unreadUser.getUser().getName());
							} else {
								userService.initParameters();
								userService.setParameters(UserService.set_uid, uid);
								TransferResultInfo<?> rs_user = userService.find(UserService.findMode_summary);
								if(ResultCodeStorage.type_success.equals(rs_user.getMsgType())) {
									@SuppressWarnings("unchecked")
									List<TransferUserInfo> list_user = (List<TransferUserInfo>) rs_user.getMsgContent();
									for (TransferUserInfo transferUserInfo : list_user) {
										unreadInfo.setUserName(transferUserInfo.getName());
									}
								}
							}
							int count = map_unread.get(uid);
							unreadInfo.setCount(count);
							list_unread.add(unreadInfo);
						}
					}
					TransferResultInfo<List<TransferUserUnreadInfo>> rs = new TransferResultInfo<List<TransferUserUnreadInfo>>();
					rs.setMsgType(ResultCodeStorage.type_success);
					rs.setMsgCode(ResultCodeStorage.code_success);
					rs.setMsgContent(list_unread);
					sendMsgtoWeb(rs);
				}
				}
			}
			break;
			default: {
				throw new IllegalParameterException("op 参数无效");
			}
			}
		} catch (NoLoginException e) {
			e.printStackTrace();
			TransferResultInfo<String> rs = new TransferResultInfo<String>();
			rs.setMsgType(ResultCodeStorage.type_error);
			rs.setMsgCode(ResultCodeStorage.code_err_no_login);
			rs.setMsgContent(StringUtil.formatResultInfoMessage(ResultCodeStorage.code_err_no_login, e.getMessage()));
			sendMsgtoWeb(rs);
		} catch (IllegalParameterException e) {
			// TODO: handle exception
			e.printStackTrace();
			TransferResultInfo<String> rs = new TransferResultInfo<String>();
			rs.setMsgType(ResultCodeStorage.type_error);
			rs.setMsgCode(ResultCodeStorage.code_err_invalid_parameter);
			rs.setMsgContent(StringUtil.formatResultInfoMessage(ResultCodeStorage.code_err_invalid_parameter, e.getMessage()));
			sendMsgtoWeb(rs);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			TransferResultInfo<String> rs = new TransferResultInfo<String>();
			rs.setMsgType(ResultCodeStorage.type_error);
			rs.setMsgCode(ResultCodeStorage.code_err_generic_server_internal_exception);
			rs.setMsgContent(StringUtil.formatResultInfoMessage(ResultCodeStorage.code_err_generic_server_internal_exception, e.getMessage()));
			sendMsgtoWeb(rs);
		}
	}
	
	private void checkUserLogin() throws NoLoginException {
		HttpSession session = request.getSession();
		if (session.getAttribute(Configurations.session_user_login_key) == null
				|| Configurations.interceptor_string_nologin.equals(session.getAttribute(Configurations.session_user_login_key))) {
			session.removeAttribute(Configurations.session_user_login_key);
			throw new NoLoginException("普通用户没有登录");
		}
	}
	
	private void checkAdminLogin() throws NoLoginException {
		HttpSession session = request.getSession();
		if (session.getAttribute(Configurations.session_admin_login_key) == null
				|| Configurations.interceptor_string_nologin.equals(session.getAttribute(Configurations.session_admin_login_key))) {
			session.removeAttribute(Configurations.session_admin_login_key);
			throw new NoLoginException("管理员没有登录");
		}
	}
	
	private void sendMsgtoWeb(TransferResultInfo<?> result) {
		response.setContentType("text/javascript");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control","no-cache");
		PrintWriter out;
		try {
			out = response.getWriter();
			Gson gson = new Gson();
			out.print(gson.toJson(result));
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			TransferResultInfo<String> rs = new TransferResultInfo<String>();
			rs.setMsgType(ResultCodeStorage.type_error);
			rs.setMsgCode(ResultCodeStorage.code_err_generic_server_internal_exception);
			rs.setMsgContent(StringUtil.formatResultInfoMessage(ResultCodeStorage.code_err_generic_server_internal_exception, e.getMessage()));
			sendMsgtoWeb(rs);
		}
	}
	
}

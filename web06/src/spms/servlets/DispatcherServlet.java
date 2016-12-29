package spms.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import spms.controls.Controller;
import spms.controls.LogInController;
import spms.controls.LogOutController;
import spms.controls.MemberAddController;
import spms.controls.MemberDeleteController;
import spms.controls.MemberListController;
import spms.controls.MemberUpdateController;
import spms.vo.Member;

@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
  @Override
  protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    String servletPath = request.getServletPath();
    try {
    	
     Controller controller = null;
     Map<String, Object> model = new HashMap<String,Object>();
     
     ServletContext sc = this.getServletContext();
     // model 안에 memberDao 주입
     // 문제: 1 모든 컨트롤러가 dao 를 주입받게 된다.   
    // model.put("memberDao",sc.getAttribute("memberDao"));
     //MemberDao 객체는ㄴ 더이상 map 객체에 담을 필요가 없어서 제거한다.
     
     // begin - 요청분기  &  pageControllerPath 결정
     controller = (Controller)sc.getAttribute(servletPath);
      if ("/member/list.do".equals(servletPath)) {
    	  System.out.println("listTest");
      } else if ("/member/add.do".equals(servletPath)) {
        if (request.getParameter("email") != null) {
         model.put("member", new Member()
            .setEmail(request.getParameter("email"))
            .setPassword(request.getParameter("password"))
            .setName(request.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
        if (request.getParameter("email") != null) {
         model.put("member", new Member()
            .setNo(Integer.parseInt(request.getParameter("no")))
            .setEmail(request.getParameter("email"))
            .setName(request.getParameter("name")));
        }else{
        	model.put("no", new Integer(request.getParameter("no")));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
    	  model.put("no", new Integer(request.getParameter("no")));
      } else if ("/auth/login.do".equals(servletPath)) {
    	 if (request.getParameter("email") != null) {
    		 model.put("loginInfo", new Member()
    				 .setEmail(request.getParameter("email"))
    				 .setPassword(request.getParameter("password")));
    	 }
      }
      // end - 요청분기 & pageControllerPath 결정
     
     // 컨트롤러 호출을 통해  view를 요청
      String viewUrl = controller.execute(model);
      	System.out.println(viewUrl+"what return?");
      
      	//map -> request.attribute
      for(String key : model.keySet()) {
    	  request.setAttribute(key,model.get(key));
      }
      
      if (viewUrl.startsWith("redirect:")) {
        response.sendRedirect(viewUrl.substring(9));
        return;
      } else {
        RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
        rd.include(request, response);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    }
  }
}

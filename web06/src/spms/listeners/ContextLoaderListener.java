package spms.listeners;

// 서버에서 제공하는 DataSource 사용하기
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import spms.controls.LogInController;
import spms.controls.LogOutController;
import spms.controls.MemberAddController;
import spms.controls.MemberDeleteController;
import spms.controls.MemberListController;
import spms.controls.MemberUpdateController;
import spms.dao.MemberDao;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();
      
      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup(
          "java:comp/env/jdbc/studydb");
      
      MemberDao memberDao = new MemberDao();
      // DataSource를 Dao 에 주입
      memberDao.setDataSource(ds);
      
      /*
       * 1. 공통 저장소 : dao
       * 2. 공통 저장소 : controller(+dao)
      */
     // sc.setAttribute("memberDao", memberDao);

      //자기 요청 주소로 맵핑 되어 있다.
      sc.setAttribute("/member/list.do",new MemberListController().setMemberDao(memberDao));
      sc.setAttribute("/member/add.do",new MemberAddController().setMemberDao(memberDao));
      sc.setAttribute("/member/update.do", new MemberUpdateController().setmemberDao(memberDao));
      sc.setAttribute("/member/delete.do", new MemberDeleteController().setMemberDao(memberDao));
      sc.setAttribute("/auth/login.do", new LogInController().setMemberDao(memberDao));
      sc.setAttribute("/auth/logout.do",  new LogOutController());
   
    
    } catch(Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {}
}

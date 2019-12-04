package cn.edu.sdjzu.xg.bysj.basic;

import cn.edu.sdjzu.xg.bysj.domain.Department;
import cn.edu.sdjzu.xg.bysj.domain.School;
import cn.edu.sdjzu.xg.bysj.service.DepartmentService;
import cn.edu.sdjzu.xg.bysj.service.SchoolService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import util.JSONUtil;

import javax.print.DocFlavor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

/**
 * 将所有方法组织在一个Controller(Servlet)中
 */
@WebServlet("/department.ctl")
public class DepartmentController extends HttpServlet {
    //请使用以下JSON测试增加功能（id为空）
    //{"description":"id为null的新专业","no":"01","remarks":"good","school":{"description":"管理","id":1,"no":"01","remarks":"the best"}}
    //请使用以下JSON测试修改功能
    //{"description":"修改id为null的新专业","no":"01","id":8,"remarks":"good","school":{"description":"管理","id":1,"no":"01","remarks":"the best"}}
    /**
     * POST, http://172.81.226.11 :8080/bysj1837/department.ctl, 增加专业
     * 增加一个专业对象：将来自前端请求的JSON对象，增加到数据库表中
     *
     * @param request  请求对象
     * @param response 响应对象
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        //根据request对象，获得代表参数的JSON字串
        String department_json = JSONUtil.getJSON(request);

        //将JSON字串解析为Department对象
        Department departmentToAdd = JSON.parseObject(department_json, Department.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加Department对象
        try {
            DepartmentService.getInstance().add(departmentToAdd);
            message.put("message", "增加成功");
        } catch (SQLException e) {
            message.put("message", "数据库操作异常");
        } catch (Exception e) {
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * DELETE, http://172.81.226.11 :8080/bysj1837/department.ctl?id=1, 删除id=1的专业
     * 删除一个专业对象：根据来自前端请求的id，删除数据库表中id的对应记录
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //读取参数id
        String id_str = request.getParameter("id");
        int id = Integer.parseInt(id_str);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();

        //到数据库表中删除对应的专业
        try {
            DepartmentService.getInstance().delete(id);
            message.put("message", "删除成功");
        } catch (SQLException e) {
            message.put("message", "数据库操作异常");
        } catch (Exception e) {
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }


    /**
     * PUT, http://172.81.226.11 :8080/bysj1837/department.ctl, 修改专业
     * <p>
     * 修改一个专业对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String department_json = JSONUtil.getJSON(request);
        //将JSON字串解析为Department对象
        Department departmentToAdd = JSON.parseObject(department_json, Department.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改Department对象对应的记录
        try {
            DepartmentService.getInstance().update(departmentToAdd);
            message.put("message", "修改成功");
        } catch (SQLException e) {
            message.put("message", "数据库操作异常");
        } catch (Exception e) {
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * GET, http://172.81.226.11 :8080/bysj1837/department.ctl?id=1, 查询id=1的专业
     * GET, http://172.81.226.11 :8080/bysj1837/department.ctl, 查询所有的专业
     * 把一个或所有专业对象响应到前端
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");
        String school_id_str = request.getParameter("school_id");
        String paraType = request.getParameter("paraType");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null, 表示响应所有专业对象，否则响应id指定的专业对象
            //如果paraType为school类型且指定school_id_str，响应school_id_str对应的对象
            if (id_str == null) {
                if (school_id_str == null){
                    responseDepartments(response);
                }
                else if (paraType.equals("school")){
                    int id = Integer.parseInt(school_id_str);
                    responseSchool(id,response);
                }
                else {
                    response.getWriter().println("错误类型");
                }
            }else {
                int id = Integer.parseInt(id_str);
                responseDepartment(id, response);
            }
        } catch (SQLException e) {
            message.put("message", "数据库操作异常");
            //响应message到前端
            response.getWriter().println(message);
        } catch (Exception e) {
            message.put("message", "网络异常");
            //响应message到前端
            response.getWriter().println(message);
        }
    }
    private void responseSchool(int school_id, HttpServletResponse response)throws IOException,SQLException{
        //根据school_id寻找对象
        Collection<Department> departments = DepartmentService.getInstance().findAllBySchool(school_id);
        String school_json = JSON.toJSONString(departments);
        //响应department_json到前端
        response.getWriter().println(school_json);
    }
    //响应一个专业对象
    private void responseDepartment(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找专业
        Department department = DepartmentService.getInstance().find(id);
        String department_json = JSON.toJSONString(department);

        //响应department_json到前端
        response.getWriter().println(department_json);
    }

    //响应所有专业对象
    private void responseDepartments(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有专业
        Collection<Department> departments = DepartmentService.getInstance().getAll();
        String departments_json = JSON.toJSONString(departments, SerializerFeature.DisableCircularReferenceDetect);
        //响应departments_json到前端
        response.getWriter().println(departments_json);
    }
}

package resource;

import com.cl.api.service.Dut2Service;
import com.cl.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by TF on 2016/8/8.
 */
@Path("d")
@Produces({MediaType.APPLICATION_JSON})
public class DResource {

    @Autowired
    public Dut2Service dut2Service;

    @Autowired
    public UserService userService;



    @GET
    @Path("test")
    public Response test(){
        String s =  dut2Service.test();
        return Response.ok(s).build();
    }

    @GET
    @Path("user")
    public Response user(){
        return Response.ok(userService.user()).build();
    }
}

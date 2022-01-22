package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import whzz.pojo.UpLimit;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "limit")
public interface LimitClient {
    @RequestMapping(value = "/limit/saveAll", method = RequestMethod.POST)
    String saveLimits(@RequestBody List<UpLimit> limits);
}

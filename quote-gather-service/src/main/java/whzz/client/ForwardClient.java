package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import whzz.pojo.Daily;
import whzz.pojo.Forward;

import java.util.List;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "forward")
public interface ForwardClient {
    @RequestMapping(value = "/forward/saveAll", method = RequestMethod.POST)
    String saveForwards(@RequestBody List<Forward> forwards);

    @RequestMapping(value = "/forward/saveAll", method = RequestMethod.POST)
    String saveDailies(@RequestBody List<Daily> dailies);

    @GetMapping(value = "/forward/get/{code}")
    List<Forward> getForwardsByCode(@PathVariable("code") String code);

    @GetMapping(value = "/forward/delete/{code}")
    String deleteByCode(@PathVariable("code") String code);
}

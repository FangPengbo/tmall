package com.fang.controller.fore;

import com.alibaba.fastjson.JSONObject;
import com.fang.controller.BaseController;
import com.fang.entity.Address;
import com.fang.service.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 加载地址信息
 *      方鹏博
 */
@Controller
public class ForeAddressController extends BaseController {
    @Resource(name = "addressService")
    private AddressService addressService;


    /**
     * 根据address_areaId获取地址信息-ajax
     * @param areaId 省id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "address/{areaId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    protected String getAddressByAreaId(@PathVariable String areaId) {
        JSONObject object = new JSONObject();
        logger.info("获取AreaId为{}的地址信息",areaId);
        //获取省下的市集合
        List<Address> addressList = addressService.getList(null, areaId);
        if (addressList == null || addressList.size() <= 0) {
            object.put("success", false);
            return object.toJSONString();
        }
        logger.info("获取该地址可能的子地址信息");
        //获取第一个市下的区集合
        List<Address> childAddressList = addressService.getList(null, addressList.get(0).getAddress_areaId());
        object.put("success", true);
        object.put("addressList", addressList);
        object.put("childAddressList", childAddressList);
        return object.toJSONString();
    }



}

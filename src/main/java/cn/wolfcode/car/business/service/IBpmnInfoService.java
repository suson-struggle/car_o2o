package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 岗位服务接口
 */
public interface IBpmnInfoService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo);

    /**
     * 流程图文件上传
     * @param file
     * @return
     */
    String uploadBpmnFile(MultipartFile file) throws IOException;

    /**
     * 部署流程定义
     * @param path
     * @param bpmnType
     * @param info
     */
    void deploy(String path, String bpmnType, String info) throws IOException;

    /**
     * 删除流程定义
     * @param id
     */
    void delete(Long id);

    /**
     * 查看流程图文件
     * @param deploymentId
     * @param type
     * @return
     */
    InputStream readResource(String deploymentId, String type);

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    List<BpmnInfo> queryByType(String type);
}

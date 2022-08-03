package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.poi.ss.formula.functions.Odd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServiceItemServiceImpl implements IServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;
    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;
    @Autowired
    private RuntimeService runtimeService;


    @Override
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<ServiceItem>(serviceItemMapper.selectForList(qo));
    }


    @Override
    public void save(ServiceItem serviceItem) {
        // 解决前端传入额外参数的问题，创建一个新的对象，并设置该对象的属性值为用户传入的指定的属性值
        // 需要设置哪些属性就设置哪些属性
        ServiceItem newItem = new ServiceItem();
        newItem.setName(serviceItem.getName());
        newItem.setOriginalPrice(serviceItem.getOriginalPrice());
        newItem.setDiscountPrice(serviceItem.getDiscountPrice());
        newItem.setCarPackage(serviceItem.getCarPackage());
        newItem.setInfo(serviceItem.getInfo());
        newItem.setServiceCatalog(serviceItem.getServiceCatalog());

        // 为 createTime auditStatus saleStatus 设置值
        newItem.setCreateTime(new Date());
        if (ServiceItem.CARPACKAGE_YES.equals(serviceItem.getCarPackage())) {
            // 如果是套餐，则设置审核状态为初始化
            newItem.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);
        } else {
            // 如果不是套餐，则设置审核状态为无需审核
            newItem.setAuditStatus(ServiceItem.AUDITSTATUS_NO_REQUIRED);
        }

        serviceItemMapper.insert(newItem);
    }

    @Override
    public ServiceItem get(Long id) {
        return serviceItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(ServiceItem serviceItem) {
        // 根据用户传入的 id ，查询出现有的数据库中的服务单项的数据
        // 因为前端没有让用户输入是否是套餐，也不允许设置
        ServiceItem oldItem = this.get(serviceItem.getId());
        // 设置审核状态为之前的审核状态
        serviceItem.setAuditStatus(oldItem.getAuditStatus());

        // 如果服务项是正在审核中或是已上架，不允许修改，抛出异常
        if (ServiceItem.AUDITSTATUS_AUDITING.equals(oldItem.getAuditStatus()) ||
                ServiceItem.SALESTATUS_ON.equals(oldItem.getSaleStatus())) {
            throw new BusinessException("非法操作");
        }

        // 如果服务单项是套餐，且审核状态为通过，则修改其审核状态为初始化
        if (ServiceItem.CARPACKAGE_YES.equals(oldItem.getCarPackage()) &&
                ServiceItem.AUDITSTATUS_APPROVED.equals(oldItem.getAuditStatus())) {
            serviceItem.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);
        }

        serviceItemMapper.updateByPrimaryKey(serviceItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            serviceItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<ServiceItem> list() {
        return serviceItemMapper.selectAll();
    }

    @Override
    public void saleOn(Long id) {
        // 根据 id 获取服务单项的数据
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);

        // 如果当前已是上架状态则抛出异常
        if (ServiceItem.SALESTATUS_ON.equals(serviceItem.getSaleStatus())) {
            throw new BusinessException("已经上架了！");
        }

        // 如果当前服务单项是套餐，且审核状态为非审核通过，则抛出异常
        if (ServiceItem.CARPACKAGE_YES.equals(serviceItem.getCarPackage()) &&
                !ServiceItem.AUDITSTATUS_APPROVED.equals(serviceItem.getAuditStatus())) {
            throw new BusinessException("非法操作");
        }

        // 修改服务单项的上架状态
        serviceItemMapper.changeSaleStatus(id, ServiceItem.SALESTATUS_ON);
    }

    @Override
    public void saleOff(Long id) {
        // 根据 id 获取服务单项的数据
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);

        // 如果当前已是下架状态则抛出异常
        if (ServiceItem.SALESTATUS_OFF.equals(serviceItem.getSaleStatus())) {
            throw new BusinessException("已经下架了！");
        }

        // 修改服务单项的下架状态
        serviceItemMapper.changeSaleStatus(id, ServiceItem.SALESTATUS_OFF);
    }

    @Override
    public void startAudit(CarPackageAuditVO vo) {
        // 1、做合理化验证
        // 根据 id 查询出数据
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(vo.getId());
        // 如果不是套餐，则抛出异常
        if (!ServiceItem.CARPACKAGE_YES.equals(serviceItem.getCarPackage())) {
            throw new BusinessException("非法操作");
        }
        // 如果不是初始化而重新调整的状态，则抛出异常
        if (!(ServiceItem.AUDITSTATUS_INIT.equals(serviceItem.getAuditStatus()) ||
                ServiceItem.AUDITSTATUS_REPLY.equals(serviceItem.getAuditStatus()))) {
            throw new BusinessException("非法操作");
        }

        // 2、创建对象，并封装数据，插入到数据库中
        CarPackageAudit carPackageAudit = new CarPackageAudit();
        carPackageAudit.setInfo(vo.getInfo());
        carPackageAudit.setAuditorId(vo.getDirector());   // 店长进行审核
        carPackageAudit.setCreateTime(new Date());
        carPackageAudit.setCreator(ShiroUtils.getUser().getUserName());
        carPackageAudit.setServiceItemId(serviceItem.getId());
        carPackageAudit.setServiceItemInfo(serviceItem.getInfo());
        carPackageAudit.setServiceItemPrice(serviceItem.getDiscountPrice());
        carPackageAudit.setBpmnInfoId(vo.getBpmnInfoId());
        // 插入数据到数据库中
        carPackageAuditMapper.insert(carPackageAudit);

        // 3、设置流程变量
        // 根据 id 查询出 bpmnInfo 的数据
        Map<String, Object> map = new HashMap<>();
        map.put("director", vo.getDirector());  // 店长
        // 当折扣金额小于 3000 时，不需要财务专员进行审核
        if (vo.getFinance() != null) {
            map.put("finance", vo.getFinance());    // 财务专员
        }
        // 设置折扣价，activiti 不支持 BigDecimal 类型，因此需要转成 Long 类型
        map.put("discountPrice", serviceItem.getDiscountPrice().longValue());

        // 4、开启流程实例，设置流程变量和 businessKey
        // 根据 id 查询 bpmnInfo 的数据
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(vo.getBpmnInfoId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), carPackageAudit.getId().toString(), map);

        carPackageAudit.setInstanceId(processInstance.getId());
        // 5、更新 carPackageAudit 的数据，修改 instance_id 字段
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);

        // 6、修改服务单项的状态为审核中的状态
        serviceItemMapper.changeAuditStatus(vo.getId(), ServiceItem.AUDITSTATUS_AUDITING);
    }
}

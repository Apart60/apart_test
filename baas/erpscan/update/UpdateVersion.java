package erpscan.update;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import erpscan.save.Pdacommon;
import erpscan.Fileoperate;

import erpscan.Common;
import erpscan.newstep.Newstep;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Row;
import com.justep.baas.data.Table;

public class UpdateVersion {
	private static final String DATASOURCE = Common.DATASOURCE;

	public static JSONObject updateversion(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		Double newversion = params.getDouble("newversion");
		String message = "";
		try {
			System.out.println("updateversion erp begin");
			Double version = null;
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			Table companytalbe = DataUtils.queryData(conn, "select confid,version from sysconfigure ", null, null, null, null);
			String confid = "";
			if (companytalbe.getRows().size() > 0) {
				confid = companytalbe.getRows().get(0).getString("confid");
				version = Double.parseDouble(companytalbe.getRows().get(0).getValue("version").toString());
			}
			System.out.println("newversion:" + newversion + " " + version);
			if (newversion != null && version != null && newversion > version) {
				if (version < 1.1 && newversion >= 1.1) {
					System.out.println("update:v1.1");
					// MRP 功能相关SQL语句
					ps.addBatch("CREATE TABLE IF NOT EXISTS `mrpconfig` (`companyid` varchar(36) NOT NULL COMMENT '企业编号', `purchase_na` int(11) DEFAULT '0' COMMENT '采购订单未审+',`purchase_a` int(11) DEFAULT '0' COMMENT '采购在订+',`salesorder_na` int(11) DEFAULT '0' COMMENT '销售订单未审-',`salesorder_a` int(11) DEFAULT '0' COMMENT '销售在订-',`scheduleorder_na` int(11) DEFAULT '0' COMMENT '排产未审+',`scheduleorder_a` int(11) DEFAULT '0' COMMENT '生产中+',`productnotget` int(11) DEFAULT '0' COMMENT '生产未领料-',`outsourcing_p_na` int(11) DEFAULT '0' COMMENT '委外产品未审+',`outsourcing_p_a` int(11) DEFAULT '0' COMMENT '委外生产中+',`outsourcing_m_na` int(11) DEFAULT '0' COMMENT '委外材料未审-',`outsourcing_m_a` int(11) DEFAULT '0' COMMENT '委外材料待发-',`safetystock` int(11) DEFAULT '0' COMMENT '安全库存-',`tempstore` int(11) DEFAULT '0' COMMENT '暂存占数量+',`store` int(11) DEFAULT '0' COMMENT '所有仓库',`storelist` text COMMENT '仓库id列表',`type` int(11) DEFAULT '0' COMMENT '统计类型',PRIMARY KEY (`companyid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("INSERT INTO `mrpconfig` (`companyid`, `purchase_na`, `purchase_a`, `salesorder_na`, `salesorder_a`, `scheduleorder_na`, `scheduleorder_a`, `productnotget`, `outsourcing_p_na`, `outsourcing_p_a`, `outsourcing_m_na`, `outsourcing_m_a`, `safetystock`, `tempstore`, `store`, `storelist`, `type`) VALUES ('C87B855393E000012EF41D43CEB519BD', 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, '', 4)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8DBC1C0EDA00001B3CB1D5DCC30F980', 3, 25, '生产管理', 'orderset', 1, 'mrp管理', 'mrpmanage', 1, 1, '查看', 'mrpmanage:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-04-07 14:57:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-04-07 14:59:46', NULL),('C8DBC1DE5B0000011FEC973A78E8FC20', 3, 25, '生产管理', 'orderset', 1, 'mrp管理', 'mrpmanage', 1, 2, '计算', 'mrpmanage:math', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-04-07 14:59:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-04-07 15:00:28', 1),('C8DBC1E676A000019BBA1C101CB07500', 3, 25, '生产管理', 'orderset', 1, 'mrp管理', 'mrpmanage', 1, 3, '导出', 'mrpmanage:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-04-07 15:00:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-04-07 15:00:57', 1)");
					// 增加必需报工才可入库配置，调整如不限制，生产入库不限制。
					ps.addBatch("alter table s_company_config add mustreport int default 1 comment '必需报工才可入库'");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					// 物料需求计算 运行updateprod.w生成未审核与生产中工单产品的需领料配置。
					ps.addBatch("alter table prodrequisition_work_total add needcount int default 0 comment '需求数量'");
					ps.addBatch("alter table prodrequisition_work_total add remark  varchar(200) default 0 comment '需求备注'");
					ps.addBatch("alter table prodrequisition_work_total add goods_number int default 0 comment '序号'");
					ps.addBatch("alter table prodrequisition_work_total add scheduleid varchar(36) default 0 comment '排产单ID'");
					ps.addBatch("alter table prodrequisition_work_total add salesorderid varchar(36) default 0 comment '销售订单ID'");
					ps.addBatch("update prodrequisition_work_total p,t_order t set p.scheduleid=t.scheduleid,p.salesorderid=t.salesorderid where p.worksheetid=t.id");

					ps.addBatch("ALTER TABLE `prodrequisition_work_total` ADD INDEX `scheduleid` (`scheduleid`),ADD INDEX `salesorderid` (`salesorderid`)");

					ps.addBatch("alter table s_company_config add needitem int default 0 comment '默认需领料的类型'");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8E9A0BD43D000013E8618001CC11617', 3, 25, '生产管理', 'orderset', 1, '排产单管理', 'scheduleorderdata', 1, 15, '打印需领料配置', 'scheduleorderdata:needitemsetprint', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-05-20 17:15:56', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-05-20 17:16:38', 1),('C8E9A0BDA7D00001716C149962A11DB2', 3, 25, '生产管理', 'orderset', 1, '排产单管理', 'scheduleorderdata', 1, 14, '保存需领料配置', 'scheduleorderdata:needitemsetsave', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-05-20 17:15:57', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-05-20 17:16:45', 1),('C8E9A0BDCF500001433B8940188B8980', 3, 25, '生产管理', 'orderset', 1, '排产单管理', 'scheduleorderdata', 1, 13, '查看需领料配置', 'scheduleorderdata:needitemsetshow', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-05-20 17:15:58', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-05-20 17:16:53', 1)");

					// -- img/companyhead.jpg //打印单据表头图片上传
					ps.addBatch("ALTER TABLE `s_company_config`  ADD  `company_logo` VARCHAR(200)  DEFAULT null COMMENT 'Logo'");
					ps.addBatch("ALTER TABLE `s_company_config`  ADD  `blankline` int  DEFAULT 0 COMMENT '单据打印不显示空白行'");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `storetemplate`  ADD  `plandate` VARCHAR(20)  DEFAULT '' COMMENT '计划到货日期'");

					// -- 2020-05-27 处理工单产品最大值，重新调整可入库数量的计算。
					ps.addBatch("update t_order t  set t.canincount=round(ifnull((select round(if(min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count)>1,1,min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count))*t.max_order_count,3) as pcount from t_order_detail td left join t_progress tp on td.id = tp.detail_id and td.fstatus=1 where td.order_id=t.id group by td.order_id)-t.incount,0),3) where t.order_status=1 and t.canincount>0");

					ps.addBatch("update t_order set canincount=0  where  canincount<0");

					// -- 2020-05-28 处理生产领料汇总needcount 数据类型由int 转为double 问题。
					ps.addBatch("ALTER TABLE `prodrequisition_work_total` CHANGE COLUMN `needcount` `needcount` DOUBLE NULL DEFAULT '0' COMMENT '需求数量' AFTER `total`");

					ps.addBatch("update  prodrequisition_work_total p,t_order t,s_company_config s set  p.needcount=round(p.unitcount*t.order_count,s.countbit)  where p.worksheetid=t.id and p.companyid=s.company_id");

					ps.addBatch("alter table t_step_class add stype int(1) null default '0' comment '数据类型'");
					ps.addBatch("alter table t_step add stepnewid varchar(36) null default '' comment '数据类型'");
					ps.addBatch("update s_permission set fstatus=2 where id='C856742184A000012F6A110014601335'");

					ps.addBatch("CREATE TABLE `t_stepnew_role` ( `id` VARCHAR(36) NULL DEFAULT NULL COMMENT '主键',`companyid` VARCHAR(50) NULL DEFAULT NULL COMMENT '组织编号',`stepnewid` VARCHAR(50) NULL DEFAULT NULL COMMENT '工序编号',`role_id` VARCHAR(50) NULL DEFAULT NULL COMMENT '角色编号',`create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID',`create_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人',`create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期',UNIQUE INDEX `role_step` (`role_id`, `stepnewid`),INDEX `role_id` (`role_id`),INDEX `stepnewid` (`stepnewid`),INDEX `companyid` (`companyid`)) COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("CREATE TABLE `t_stepnew_userid` ( `id` VARCHAR(36) NULL DEFAULT NULL COMMENT '主键', `companyid` VARCHAR(50) NULL DEFAULT NULL COMMENT '组织编号', `stepnewid` VARCHAR(50) NULL DEFAULT NULL COMMENT '工序编号', `userid` VARCHAR(50) NULL DEFAULT NULL COMMENT '用户编号', `create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', UNIQUE INDEX `stepnewid_userid` (`stepnewid`, `userid`), INDEX `companyid` (`companyid`), INDEX `stepnewid` (`stepnewid`), INDEX `userid` (`userid`) ) COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("CREATE TABLE `t_stepnew` ( `stepnewid` VARCHAR(36) NULL DEFAULT NULL COMMENT '主键', `companyid` VARCHAR(50) NULL DEFAULT NULL COMMENT '组织编号', `stepnewcode` VARCHAR(50) NULL DEFAULT NULL COMMENT '工序编号', `stepnewname` VARCHAR(50) NULL DEFAULT NULL COMMENT '工序名称', `remark` VARCHAR(200) NULL DEFAULT NULL COMMENT '备注', `fstatus` INT(11) NULL DEFAULT '1' COMMENT '状态', `create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', `update_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人ID', `update_by` VARCHAR(50) NULL DEFAULT NULL COMMENT '更新人', `update_date` DATETIME NULL DEFAULT NULL COMMENT '更新日期', INDEX `create_id` (`create_id`), INDEX `companyid` (`companyid`) ) COLLATE='utf8_general_ci' ENGINE=InnoDB ");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8EB36F765100001EF71827A11101159', 3, 50, '基础模块', 'basicset', 91, '工序管理', 'new_stepmanage', 1, 1, '新增', 'new_stepmanage:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:35:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:37:05', NULL),"
							+ "('C8EB37292F0000013835176017B087E0', 3, 50, '基础模块', 'basicset',  85, '工序管理', 'new_stepmanage', 1, 2, '编辑', 'new_stepmanage:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:38:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:38:52', 1),"
							+ "('C8EB372CDB0000011C3611D614751B93', 3, 50, '基础模块', 'basicset',  85, '工序管理', 'new_stepmanage', 1, 3, '复制新增', 'new_stepmanage:copy', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:38:55', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:39:12', 1),"
							+ "('C8EB373399800001B1561F901AEB1852', 3, 50, '基础模块', 'basicset',  85, '工序管理', 'new_stepmanage', 1, 4, '删除', 'new_stepmanage:del', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:39:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:39:38', 1),"
							+ "('C8EB373AD280000159CA12D0D9FB6400', 3, 50, '基础模块', 'basicset',  85, '工序管理', 'new_stepmanage', 1, 5, '批量删除用户配置', 'new_stepmanage:chosedel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:39:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:40:02', 1),"
							+ "('C8EB373E41800001D4B39EF030B04C30', 3, 50, '基础模块', 'basicset',  85, '工序管理', 'new_stepmanage', 1, 6, '用户配置', 'new_stepmanage:userset', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:40:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:40:26', 1),"
							+ "('C8EB37449B800001869225703E3D12E8', 3, 50, '基础模块', 'basicset',  85, '工序管理', 'new_stepmanage', 1, 7, '导入', 'new_stepmanage:import', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:40:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:41:12', 1),"
							+ "('C8EE8B10A0B00001134462B0177F2F40', 3, 100, '系统管理', 'systemset', 30, '用户管理', 'userdata', 1, 10, '工序配置', 'userdata:stepset', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-06-04 23:46:46', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-06-04 23:47:23', 1),"
							+ "('C8EE91DC53B00001B18B86D03255129B', 3, 50, '基础模块', 'basicset', 85, '工序管理', 'new_stepmanage', 1, 0, '查看', 'new_stepmanage:read', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-06-05 01:45:32', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-06-05 01:48:24', 1),"
							+ "('C8EB37537C70000129AD1120587016C6', 3, 50, '基础模块', 'basicset', 91, '工序管理', 'new_stepmanage', 1, 8, '状态', 'new_stepmanage:status', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:41:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-05-25 15:41:55', 1)");

					ps.addBatch("create or replace view newstep_usercountlist as select ts.*, (select count(id) from t_stepnew_userid tsu where tsu.stepnewid = ts.stepnewid) as usercount from t_stepnew ts ");

					// -- 2020-06-05 erp 生产日报等增加相关工艺状态。
					ps.addBatch("CREATE OR REPLACE VIEW t_reportbase_v as select  p.*,c.customercode,c.customername,o.order_count,o.max_order_count,od.item_count,od.max_item_count,od.item_remark,od.id as item_id,od.itemid,od.batchno,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,o.iproperty,s.class_id,sc.class_name,sc.id as classid,sc.fstatus as class_status,s.step_code,s.step_name,s.step_no ,ifnull(d.device_id,'') as device_code,ifnull(d.device_name,'') as device_name,sf.staffname,sf.staffcode, ifnull(ws.workshop_id,'') as workshop_code,ifnull(ws.workshop_name,'') as workshop_name ,o.order_time,o.finish_time as forder_time,od.goods_number,o.plandate,o.order_remark,o.order_status,o.schedulestatus,o.scheduletype,o.billno,o.scheduleid,o.orderid,o.salesorderid,o.order_id as saleordercode,u.realname from t_order_progress p left join t_order o on p.order_id = o.id  left join t_order_detail od on p.detail_id = od.id left join iteminfo im on im.itemid=od.itemid left join t_step s on p.step_id = s.id left join t_device d on p.device_id = d.id left join customer c on o.customer_id = c.customerid left join t_step_class sc on s.class_id = sc.id left join staffinfo sf on p.user_id = sf.staffid  left join t_workshop ws on p.workshop_id = ws.id   left join s_userinfo u on u.userid = p.create_id");

					// -- 2020-06-06 处理MRP所下单iproperty为null值问题。
					ps.addBatch("update scheduleorder s set iproperty=ifnull((select REPLACE(group_concat(it.propertyname,',',it.propertyshow,';'),';,',';') from itemproperty as it where it.status='1' and it.companyid=s.companyid),'') where s.iproperty is null or s.iproperty='null'");
					ps.addBatch("update purchaseorder s set iproperty=ifnull((select REPLACE(group_concat(it.propertyname,',',it.propertyshow,';'),';,',';') from itemproperty as it where it.status='1' and it.companyid=s.companyid),'') where s.iproperty is null or s.iproperty='null'");
					ps.addBatch("update outsourcing s set iproperty=ifnull((select REPLACE(group_concat(it.propertyname,',',it.propertyshow,';'),';,',';') from itemproperty as it where it.status='1' and it.companyid=s.companyid),'') where s.iproperty is null or s.iproperty='null'");
					ps.addBatch("update t_order s set iproperty=ifnull((select REPLACE(group_concat(it.propertyname,',',it.propertyshow,';'),';,',';') from itemproperty as it where it.status='1' and it.companyid=s.companyid),'') where s.iproperty is null or s.iproperty='null'");

					// -- sql20200609 后端报工与返工报备功能
					ps.addBatch("CREATE TABLE `uploadimage` ( `id` VARCHAR(36) NOT NULL COMMENT 'id', `Url` VARCHAR(200) NULL DEFAULT NULL COMMENT '储存路径',`img_name` VARCHAR(100) NULL DEFAULT NULL, `detail_id` VARCHAR(36) NULL DEFAULT NULL, `order_id` VARCHAR(36) NULL DEFAULT NULL, `order_progressid` VARCHAR(36) NULL DEFAULT NULL, `scan_type` INT(11) NULL DEFAULT NULL, `create_date` DATETIME NULL DEFAULT NULL, `create_id` VARCHAR(36) NULL DEFAULT NULL, `create_by` VARCHAR(30) NULL DEFAULT NULL, PRIMARY KEY (`id`), INDEX `detail_id` (`detail_id`), INDEX `order_id` (`order_id`), INDEX `order_progressid` (`order_progressid`), INDEX `create_id` (`create_id`) )");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8EE68F26AF00001BCE7FBB41380133A', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 11, '后端报工', 'orderdata:reportsave', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-04 13:50:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-04 13:50:48', 1),('C8EF182F0F900001BCE43420173E1DE2', 3, 25, '生产管理', 'orderset', 90, '返工报备记录', 'returncontentdata', 1, 3, '新增', 'returncontentdata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-06 16:53:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-06 16:53:11', 1)");

					// -- sql20200609 增加报工方式类型
					ps.addBatch("alter table t_order_progress add reporttype int DEFAULT 0  COMMENT '报工方式'");

					ps.addBatch("CREATE OR REPLACE  VIEW t_reportbase_v  as select  p.*,c.customercode,c.customername,o.order_count,o.max_order_count,od.item_count,od.max_item_count,od.item_remark,od.id as item_id,od.itemid,od.batchno,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,o.iproperty,s.class_id,sc.class_name,sc.id as classid,sc.fstatus as class_status,s.step_code,s.step_name,ifnull(d.device_id,'') as device_code,ifnull(d.device_name,'') as device_name,sf.staffname,sf.staffcode,ifnull(ws.workshop_id,'') as workshop_code,ifnull(ws.workshop_name,'') as workshop_name ,o.order_time,o.finish_time as forder_time,od.goods_number,o.plandate,o.order_remark,o.order_status,o.schedulestatus,o.scheduletype,o.billno,o.scheduleid,o.orderid,o.salesorderid,o.order_id as saleordercode,u.realname from t_order_progress p left join t_order o on p.order_id = o.id left join t_order_detail od on p.detail_id = od.id left join iteminfo im on im.itemid=od.itemid  left join t_step s on p.step_id = s.id  left join t_device d on p.device_id = d.id left join customer c on o.customer_id = c.customerid  left join t_step_class sc on s.class_id = sc.id  left join staffinfo sf on p.user_id = sf.staffid  left join t_workshop ws on p.workshop_id = ws.id   left join s_userinfo u on u.userid = p.create_id");

					// -- 20200615 批量处理
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8EFAB84CEB000013F5AB9AF1F70A610', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 24, '批量启/停用', 'iteminfodata:batchenableordisable', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-08 11:47:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-08 11:48:29', 1),"
							+ "('C8F09E20F6300001313BD368FF90F610', 3, 50, '基础模块', 'basicset', 50, '仓库管理', 'housedata', 1, 5, '导入', 'housedata:import', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:27:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:28:04', 1),"
							+ "('C8F09E2995B000013E988AFF13001035', 3, 50, '基础模块', 'basicset', 93, '设备信息', 'devicedata', 1, 6, '导入', 'devicedata:import', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:28:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:28:34', 1),"
							+ "('C8F09E2E584000012CAF97D018901108', 3, 50, '基础模块', 'basicset', 96, '车间信息', 'workshopdata', 1, 4, '导入', 'workshopdata:import', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:28:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:28:53', 1),"
							+ "('C8F09E3622300001FB49F442A9301AB7', 3, 50, '基础模块', 'basicset', 80, '员工管理', 'staffdata', 1, 9, '导入', 'staffdata:import', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:29:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-11 10:29:31', 1),"
							+ "('C8F10AC1B4B00001225DB8ED161B1FF0', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 25, '查看Bom', 'iteminfodata:Bomshow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-12 18:06:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-12 18:07:04', 1)");

					// --20200627 商品增加默认往来单位
					ps.addBatch("alter table iteminfo add `customerid` VARCHAR(50) NULL DEFAULT '' COMMENT '默认往来单位'");
					ps.addBatch("create or replace view item_class_view as SELECT im.*,ifnull(cs.classname,'') as classname ,ifnull(tc.class_name,'') as class_name from iteminfo im left join itemclass cs on im.classid=cs.classid left join t_step_class tc on im.class_id=tc.id");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8F48BE85FC000013B103B108D606820', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 27, '批量变更往来单位', 'iteminfodata:changecustomer', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-23 15:25:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-06-23 15:25:15', 1)");
					ps.addBatch("ALTER TABLE `itemsplits` ADD INDEX `combitemid` (`combitemid`, `splittype`)");

					// //汇达排产明细打印增加 begin
					// ps.addBatch("alter table t_order add premark varchar(50) not null default '' comment '打印备注'");
					// ps.addBatch("alter table t_order_detail add premark varchar(50) not null default '' comment '打印备注'");
					//
					// ps.addBatch("create or replace view t_order_detail_item_view as select  tod.id,tod.fstatus,tod.companyid,tod.order_id,tod.class_id,tod.item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.scheduletype,tod.printing,tod.billno,tod.batchno,tod.must_item_count,tod.max_item_count,tod.premark,im.codeid,im.itemname,"
					// +" im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4 "
					// +" ,im.outprice5,im.splits,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,ifnull(ic.classname,'') as classname "
					// +" from t_order_detail tod left join t_step_class tsc on tod.class_id=tsc.id "
					// +" left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid ");
					//
					// ps.addBatch("create or replace view t_order_view as select   td.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.barcode,im.unit,im.imgurl,im.splits,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,ifnull(ic.classname,'') as classname,cm.customercode,cm.customername,si.staffcode,si.staffname "
					// +" from t_order td left join staffinfo si on td.operate_by=si.staffid left join customer cm on td.customer_id=cm.customerid,iteminfo im left join itemclass ic on im.classid=ic.classid where td.itemid = im.itemid order by order_status asc ");
					// //汇达排产明细打印增加 end
				}

				if (version < 1.11 && newversion >= 1.11) {
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C8F710304E300001D0E714105E001700', 3, 35, '出纳模块', 'cashiermodel', 10, '收支项目类型管理', 'inouttypemanage', 1, 6, '导入', 'inouttypemanage:import', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-07-01 11:04:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-07-01 11:04:51', 1)");
					ps.addBatch("update prodrequisition_work_total p,t_order t set p.scheduleid=t.scheduleid,p.salesorderid=t.salesorderid where p.worksheetid=t.id");
				}

				if (version < 1.12 && newversion >= 1.12) {
					// 20200704 委外加工入库计算实际加工费出错。
					ps.addBatch("update outsourcingdetail o,s_company_config c set o.actualtotal=round(ifnull((select sum(od.processmoney) from outsourcingindetail od where o.detailid=od.relationdetailid and od.`status`=1),0),c.moneybit)   where  o.stype='221' and o.`status`=1 and o.companyid=c.company_id");
					ps.addBatch("update outsourcing o,s_company_config c set o.actualtotal=round(ifnull((select sum(od.processmoney) from outsourcingindetail od where o.outsourcingid=od.relationmainid and od.`status`=1),0),c.moneybit) where  o.`status`=1 and o.companyid=c.company_id");
					ps.addBatch("update s_permission set pseq=100   where parentvalue='systemset'");
					ps.addBatch("update customer c set c.staff='' where c.staff <>'' and c.staff not in (select s.staffid from staffinfo s where c.companyid=s.companyid)");

					// 业务代理
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_userfee` ( `id` varchar(36) NOT NULL COMMENT '主键',  `out_trade_no` varchar(50) NOT NULL COMMENT '单号', `body` varchar(50) DEFAULT '' COMMENT '商品名', `total_fee` varchar(50) DEFAULT '' COMMENT '支付金额',  `transaction_id` varchar(50) DEFAULT '' COMMENT '交易号',  `time_end` varchar(50) DEFAULT '' COMMENT '支付完成时间',  `paytype` varchar(1) DEFAULT NULL COMMENT '1-微信支付 2-支付宝支付', `buyer` varchar(50) DEFAULT '' COMMENT '买家支付账号',  `paydatetime` datetime DEFAULT NULL COMMENT '支付时间', `createtime` datetime DEFAULT NULL COMMENT '创建时间',  `create_id` varchar(50) DEFAULT '' COMMENT '支付人ID', `create_by` varchar(50) DEFAULT NULL COMMENT '支付人', `companyids` varchar(200) DEFAULT NULL COMMENT '公司列表',  `companynames` varchar(500) DEFAULT NULL COMMENT '机构列表',  `unit` varchar(10) DEFAULT '' COMMENT '单位', `companycount` int(2) DEFAULT '1' COMMENT '机构数量', `scount` int(11) DEFAULT '1' COMMENT '数量', `sprice` double DEFAULT '0' COMMENT '单价', `smoney` double DEFAULT '0' COMMENT '金额', PRIMARY KEY (`id`),  UNIQUE KEY `out_trade_no` (`out_trade_no`,`transaction_id`), KEY `create_id` (`create_id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务收费表'");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_userfee_param` ( `id` varchar(36) NOT NULL COMMENT '主键', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(36) DEFAULT NULL COMMENT '创建人', `create_date` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID', `update_by` varchar(36) DEFAULT NULL COMMENT '更新人', `update_date` datetime DEFAULT NULL COMMENT '更新日期',  `fstatus` int(1) DEFAULT NULL COMMENT '状态',  `seq` int(3) DEFAULT NULL COMMENT '顺序号', `showname` varchar(50) DEFAULT NULL COMMENT '费用名',  `userid` varchar(50) DEFAULT NULL COMMENT '业务ID', `unit` varchar(50) DEFAULT NULL COMMENT '时间单位', `amount` double DEFAULT NULL COMMENT '金额', PRIMARY KEY (`id`),  KEY `userid` (`userid`), KEY `create_id` (`create_id`),  UNIQUE INDEX `userid_unit` (`userid`, `unit`),  KEY `fstatus` (`fstatus`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务收费表'");
					ps.addBatch("ALTER TABLE `s_userinfo`  ADD  `usertype` int  DEFAULT 0 COMMENT '创建类型'");
					ps.addBatch("alter table sysconfigure add  dldays int default 20 comment '业务默认试用天数'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C8F9083A38E000017A961100BA4C9E30', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 6, '查看管理用户', 'dlscompanyadmindata:backuserread', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 13:53:23', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:51:02', 1),"
							+ "('C8F90FB7B86000012FD66B004807D0D0', 1, 100, '系统管理', 'systemset', 31, '用户业务费用', 'userfeeparam', 1, 3, '删除', 'userfeeparam:delete', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 16:04:17', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 16:06:31', 1),"
							+ "('C8F90FCC20F000015FBB1B601C9F7B70', 1, 100, '系统管理', 'systemset', 31, '用户业务费用', 'userfeeparam', 1, 1, '查看', 'userfeeparam:read', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 16:05:40', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 16:05:40', 1),"
							+ "('C8F90FD39A600001327B11D311476DF0', 1, 100, '系统管理', 'systemset', 31, '用户业务费用', 'userfeeparam', 1, 2, '新增', 'userfeeparam:new', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 16:06:11', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-07 16:06:11', 1),"
							+ "('C8F95DC9A5C0000114C7E7131A9099A0', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 1, '查看', 'dlscompanyadmindata:read', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:48:39', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:48:39', 1),"
							+ "('C8F95DD555400001F6BEF67E10433680', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 2, '新增', 'dlscompanyadmindata:new', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:49:27', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:49:27', 1),"
							+ "('C8F95DDA97000001CB8EE8C03CE6D7D0', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 3, '编辑', 'dlscompanyadmindata:edit', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:49:49', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:49:49', 1),"
							+ "('C8F95DE105400001FAFFD73A16917390', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 4, '启/停用', 'dlscompanyadmindata:status', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:50:15', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:50:15', 1),"
							+ "('C8F95DE7DC40000188801C3096B012CF', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 10, '删除数据', 'dlscompanyadmindata:deletedata', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:50:43', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:10:34', 1),"
							+ "('C8F95DEEA1D00001948717FC39001938', 1, 100, '系统管理', 'systemset', 34, '业务的支付记录', 'dlpayrecorddata', 1, 2, '导出记录', 'dlpayrecorddata:exportexcel', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:51:11', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:53:39', 1),"
							+ "('C8F95DFBF2B00001989015005ED01A74', 1, 100, '系统管理', 'systemset', 34, '业务的支付记录', 'dlpayrecorddata', 1, 1, '查看', 'dlpayrecorddata:read', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:52:05', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:52:05', 1),"
							+ "('C8F95E019A300001FBEF15124893146D', 1, 100, '系统管理', 'systemset', 34, '业务的支付记录', 'dlpayrecorddata', 1, 3, '查看所有记录', 'dlpayrecorddata:allrecord', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 14:52:28', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-08 15:25:07', 1),"
							+ "('C8F9B49B5450000141C876C711C0C940', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 7, '编辑管理用户', 'dlscompanyadmindata:backuseredit', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:05:56', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:07:20', 1),"
							+ "('C8F9B4A463D000016C7D114F59C01209', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 8, '启/停管理用户', 'dlscompanyadmindata:backuserstatus', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:06:33', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:07:23', 1),"
							+ "('C8F9B4A8DF3000016852E320A7901437', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 9, '重置管理用户密码', 'dlscompanyadmindata:backusersetpwd', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:06:51', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:07:35', 1),"
							+ "('C8F9B4D5124000012312178119A9A700', 1, 100, '系统管理', 'systemset', 33, '业务的公司帐套管理', 'dlscompanyadmindata', 1, 5, '新增管理用户', 'dlscompanyadmindata:backusernew', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:09:52', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-09 16:10:10', 1)");

					// 生产任务增加
					ps.addBatch("ALTER TABLE `s_company_config`  ADD  `step_reportrefresh` int  DEFAULT 5 COMMENT 'app生产任务定时获取间隔时间'");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					// 增加删除工艺流程权限
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C8FBA4717EF00001121414F06C7AF180', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 12, '删除工艺流程', 'orderdata:delclass', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-15 16:31:18', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-07-15 16:33:08', 1)");
					// 增加上传文件限制
					ps.addBatch("alter table sysconfigure add  filesize int default 500 comment '上传文件大小'");
					ps.addBatch("update sysconfigure set filesize=-1 where 1=1");

					ps.addBatch("update datachange_log set changefunc=12 where changefunc=0 and content like '%XS-%'");
					ps.addBatch("update datachange_log set changefunc=11 where changefunc=0 and content like '%CG-%'");

				}

				if (version < 1.2 && newversion >= 1.2) {
					// 20200722 增加索引
					ps.addBatch("ALTER TABLE `houselimit` ADD INDEX `itemid` (`itemid`), ADD INDEX `houseid` (`houseid`),ADD INDEX `company_1` (`companyid`)");

					ps.addBatch("ALTER TABLE `iteminfo`  CHANGE COLUMN `property1` `property1` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性1' ,"
							+ " CHANGE COLUMN `property2` `property2` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性2' ,"
							+ " CHANGE COLUMN `property3` `property3` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性3' ,"
							+ " CHANGE COLUMN `property4` `property4` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性4' ,"
							+ " CHANGE COLUMN `property5` `property5` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性5'");
					ps.addBatch("ALTER TABLE `t_order_detail` CHANGE COLUMN `property1` `property1` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性1' AFTER `printing`,"
							+ " CHANGE COLUMN `property2` `property2` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性2' AFTER `property1`,"
							+ " CHANGE COLUMN `property3` `property3` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性3' AFTER `property2`,"
							+ " CHANGE COLUMN `property4` `property4` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性4' AFTER `property3`,"
							+ " CHANGE COLUMN `property5` `property5` VARCHAR(100) NULL DEFAULT NULL COMMENT '属性5' AFTER `property4`");

					// 超领+订单新增物料
					ps.addBatch("alter table s_company_config add `repicking` INT(11) NULL DEFAULT '1' COMMENT '超领开关',add `orderitemadd` INT(11) NULL DEFAULT '0' COMMENT '订单添加物料配置'");
					ps.addBatch("create or replace view s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");
					ps.addBatch("create or replace view t_order_view as select   td.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.barcode,im.unit,im.imgurl,im.splits,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,ifnull(ic.classname,'') as classname,ifnull(cm.customercode,'') as customercode,ifnull(cm.customername,'') as customername,si.staffcode,si.staffname from t_order td left join staffinfo si on td.operate_by=si.staffid left join customer cm on td.customer_id=cm.customerid, iteminfo im left join itemclass ic on im.classid=ic.classid where td.itemid = im.itemid order by order_status asc  ");

					// -- sql20200731 工艺工单汇总增加导出数据
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C900913F96900001735E765710A0A300', 3, 25, '生产管理', 'orderset', 50, '工艺工单汇总', 'summarysteporderdata', 1, 2, '导出数据', 'summarysteporderdata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-07-30 23:45:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-07-30 23:45:53', 1) ");

					// 更新返工报备状态记录
					ps.addBatch("update r_mainreturn o set o.finishtype = 1 where o.finishtype=0 and (select sum(p.freturn_count)-sum(p.return_count) from t_progress p where p.detail_id =o.detail_id group by p.detail_id )=0 ");

					// 更正报工user_id与create_id 调换了问题
					ps.addBatch("update t_order_progress t set t.user_id='' where t.user_id='null' or  (t.user_id<>'' and t.user_id<>'null' and t.user_id not in (select u.staffid from staffinfo u where u.companyid=t.companyid) and t.user_id not in (select u.userid from s_userinfo u where u.companyid=t.companyid)) ");
					ps.addBatch("update t_order_progress t set t.create_by=t.create_id,t.create_id=t.user_id,t.user_id=t.create_by,t.create_by=(select ifnull(u.realname,'') from s_userinfo u where u.userid=t.create_id) where t.user_id in (select u.userid from s_userinfo u where u.companyid=t.companyid) and t.create_id in (select u.staffid from staffinfo u where u.companyid=t.companyid)");
					ps.addBatch("update t_order_progress t set t.create_id = (select ifnull(u.userid,'') from staffinfo u where u.staffid=t.user_id),t.create_by=(select ifnull(u.realname,'') from s_userinfo u where u.userid=t.create_id)  where t.create_id  not in (select u.userid from s_userinfo u where u.companyid=t.companyid) and   t.user_id  in (select u.staffid from staffinfo u where u.companyid=t.companyid)");

					// -- sql20200806 增加生产任务查看权限
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C902D1343990000126C4A92019A051D0', 3, 1, 'App端', 'appdata', 7, '生产任务', 'orderworks', 1, 1, '查看', 'orderworks:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-06 23:31:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-06 23:31:50', 1)");

					// -- sql20200807 增加员工工资权限
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C902B53318100001D0617BBC1BCA3DF0', 3, 25, '生产管理', 'orderset', 91, '员工工资汇总', 'staffwagesummary', 1, 1, '查看', 'staffwagesummary:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-06 15:21:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-06 15:26:12', NULL),"
							+ "('C902B60F2100000158B812111ECEE0F0', 3, 25, '生产管理', 'orderset', 91, '员工工资汇总', 'staffwagesummary', 1, 2, '导出', 'staffwagesummary:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-06 15:36:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-06 15:36:55', 1)");

				}
				if (version < 1.3 && newversion >= 1.3) {
					// 20200814 app 商品查询 增中 工资查询
					// 2020-08-17
					// 处理mrp因没有自定义属性，把null值导致详情不能正常排产单、工单、委外加工单、采购订单的详情问题
					ps.addBatch("update t_order set iproperty='' where iproperty='null' or iproperty is null ");
					ps.addBatch("update scheduleorder set iproperty='' where iproperty='null' or iproperty is null ");
					ps.addBatch("update purchaseorder  set iproperty='' where iproperty='null' or iproperty is null ");
					ps.addBatch("update outsourcing  set iproperty='' where iproperty='null' or iproperty is null ");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C903F282262000012D45F040DE491445', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 1, '查看', 'itemsearch:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-10 11:46:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-10 11:51:59', 1),"
							+ "('C904949DF6900001F07919151CC01326', 3, 1, 'App端', 'appdata', 9, '工资查询', 'staffwage', 1, 1, '查看', 'staffwage:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-12 11:00:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-12 11:00:23', 1),('C904F621FF200001D1AB1F60D1C013A0', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 9, '工单工序打印', 'orderdata:stepprintdetail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-13 15:24:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-13 15:24:29', 1),"
							+ "('C9067EBAD7F00001F54A33D21EA0E000', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 2, '查看进货单价', 'itemsearch:inprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:45:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:48:19', 1),"
							+ "('C9067EE7E1A00001CF9E1CF09F2F5A60', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 8, '查看销售单价5', 'itemsearch:outprice5', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:48:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:39', 1),"
							+ "('C9067EED3580000127B312CD19408910', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 3, '查看零售单价', 'itemsearch:outprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:48:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:48:49', 1),"
							+ "('C9067EF004F00001FD171E4A1F1B154C', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 4, '查看销售单价1', 'itemsearch:outprice1', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:00', 1),"
							+ "('C9067EF232E000015D19EB8018D012A0', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 5, '查看销售单价2', 'itemsearch:outprice2', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:09', 1),"
							+ "('C9067EF45EE000017169183017101A6C', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 6, '查看销售单价3', 'itemsearch:outprice3', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:18', 1),"
							+ "('C9067EF6DE400001D4611100197A3020', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 7, '查看销售单价4', 'itemsearch:outprice4', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-18 09:49:28', 1)");

					// 20200901 盘点单价更新没处理好导致单价为null
					ps.addBatch("update stock set newcostprice=0 where newcostprice is null");

					ps.addBatch("ALTER TABLE `t_userfee_param` DROP INDEX `userid_unit`");

					// 20200906 委外加工退货功能相关更新语句
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C908C2E6D37000014E5E3A00D2B02500', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 1, '查看', 'outsourcingoutdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 10:44:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 10:45:05', 1),"
							+ "('C908C5AC6B700001F960BF10124D9EF0', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 2, '增加', 'outsourcingoutdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:33:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:33:32', 1),"
							+ "('C908C5B42260000165B31C751A35B350', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 3, '详情', 'outsourcingoutdata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:33:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:33:49', 1),"
							+ "('C908C5B7B170000185B914F015B010D8', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 4, '复制', 'outsourcingoutdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:33:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:34:00', 1),"
							+ "('C908C5BA60F00001F29C1BB01F1F8330', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 5, '作废', 'outsourcingoutdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:34:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:34:24', 1),"
							+ "('C908C5C00F700001E6D71B0044A09E30', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 6, '查看单价', 'outsourcingoutdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:34:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:34:37', 1),"
							+ "('C908C5C33D900001BAC0F0F0D6E11842', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 7, '导出汇总', 'outsourcingoutdata:exporttotal', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:34:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:35:02', 1),"
							+ "('C908C5C95D800001ABEAD33014D0C570', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 8, '导出明细', 'outsourcingoutdata:exportdetail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:35:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-25 11:35:12', 1),"
							+ "('C909C8A456400001AAC51440E685A0B0', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 9, '打印', 'outsourcingoutdata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-28 14:58:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-08-28 14:59:05', 1)");

					ps.addBatch("alter table outsourcingindetail add `returncount` DOUBLE NULL DEFAULT '0' COMMENT '已退数量',add `returndetailid` VARCHAR(36) NULL DEFAULT '' COMMENT '退货源加工入库单',add `returnorderid` VARCHAR(36) NULL DEFAULT '' COMMENT '退货源入库单号'");

					ps.addBatch("create or replace view outsourcingindetail_all_view as select osid.*,osd.count as ocount,osd.detailid as odetailid,osd.orderid as oorderid,osd.outsourcingid,c.customercode,c.customername,si.staffcode,si.staffname,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname,sh.housecode,sh.housename"
							+ " from outsourcingindetail osid left join customer c on osid.customerid=c.customerid  left join staffinfo si on osid.operate_by=si.staffid "
							+ " left join outsourcingdetail osd on osd.detailid=osid.relationdetailid left join storehouse sh on osid.houseid = sh.houseid,"
							+ " iteminfo im left join itemclass ic on im.classid=ic.classid  where osid.itemid=im.itemid and osid.companyid=im.companyid");
				}

				if (version < 1.31 && newversion >= 1.31) {
					// 2020-09-16 生产入库增加可选择商品，首页增加两个业务流程，销售订单增加明细查询。
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `proditem` int(1) NULL DEFAULT 0 COMMENT '生产入库可选择商品'");
					ps.addBatch("create or replace view `s_companyconfig` AS select c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");
					ps.addBatch("ALTER TABLE `salesorderdetail` CHANGE COLUMN `incount` `incount` DOUBLE NULL DEFAULT '0' COMMENT '生产已入库数' AFTER `schedulcount`,CHANGE COLUMN `outsourcingin` `outsourcingin` DOUBLE NULL DEFAULT '0' COMMENT '加工已入库数' AFTER `outsourcingcount`");

					ps.addBatch("update outsourcingindetail set salesorderdetailid='',salesorderid='',salesorderorderid=''  where salesorderdetailid='null'");
					ps.addBatch("update outsourcingindetail set returnorderid='',returndetailid=''   where returndetailid='null'");
				}

				if (version < 1.32 && newversion >= 1.32) {
					// -- sql20200926 删除Bom数据
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C912D0624BE00001802010A416E0D140', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 28, '删除Bom数据', 'iteminfodata:deleteBom', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-09-25 16:19:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-09-25 16:19:54', 1)");
				}

				if (version < 1.33 && newversion >= 1.33) {
					// 2020-10-14 增加create_id 列 工单才能正常导出
					ps.addBatch("create or replace view t_order_detail_item_all_view as select tod.id as did,tod.fstatus,tod.companyid,td.id as orderid,td.order_id,tod.class_id,tod.item_count,tod.max_item_count,tod.must_item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.printing,tod.billno,tod.batchno,tod.scheduletype,im.codeid,im.itemname,"
							+ " im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,td.iproperty,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,td.originalbill,td.operate_by,td.order_remark,td.update_by "
							+ " ,im.outprice5,im.splits,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,td.order_time,td.finish_time,td.id,ifnull(ic.classname,'') as classname,cm.customercode,cm.customername,td.create_id,td.create_by,td.create_date,td.audit_by,td.audit_date"
							+ " from t_order_detail tod left join t_step_class tsc on tod.class_id=tsc.id "
							+ " left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid "
							+ " left join t_order td on tod.order_id=td.id left join customer cm on td.customer_id=cm.customerid ");

				}
				if (version < 1.34 && newversion >= 1.34) {
					// 2020-11-04 prodrequisition_work_total
					// 删除批号列，改为只记录领退料的合计数，相关代码都要修改。
					ps.addBatch("delete t from prodrequisition_work_total t , (select CONCAT(b.worksheetid,b.itemid,b.goods_number) as name,totalid as temptotalid "
							+ " from prodrequisition_work_total b  group by b.worksheetid,b.itemid,b.goods_number  having "
							+ " count(1)>1  ) k where CONCAT(t.worksheetid,t.itemid,t.goods_number) = k.name and  t.totalid<>k.temptotalid ");

					ps.addBatch("delete t  from prodrequisition_work_total t , (select CONCAT(b.worksheetid,b.itemid) as name,totalid as "
							+ " temptotalid from prodrequisition_work_total b  group by b.worksheetid,b.itemid  having "
							+ " count(1)>1  ) k where CONCAT(t.worksheetid,t.itemid) = k.name and  t.totalid<>k.temptotalid ");

					ps.addBatch("ALTER TABLE `prodrequisition_work_total` DROP COLUMN `batchno`, DROP INDEX `companyid`");
					ps.addBatch("ALTER TABLE `prodrequisition_work_total` ADD UNIQUE INDEX `totalindex` (`worksheetid`, `itemid`)");

					ps.addBatch(" update prodrequisition_work_total t,t_order d set   t.worksheetbillno=d.billno,t.worksheetbatchno=d.batchno where t.worksheetid=d.id");

					ps.addBatch(" update prodrequisition_work_total t,s_company_config c set t.count=round(ifnull((select sum(if  "
							+ " (stype='101',count,-count)) from prodrequisitiondetail p where t.worksheetid=p.worksheetid and  "
							+ "  t.itemid=p.itemid and p.status='1'),0),c.countbit),t.total=round(ifnull((select sum(if "
							+ "  (stype='101',total,-total)) from prodrequisitiondetail p where t.worksheetid=p.worksheetid and  "
							+ "  t.itemid=p.itemid and p.status='1'),0),c.moneybit) where t.companyid=c.company_id");

					ps.addBatch("update prodrequisitiondetail p set p.relationtotalid='' where p.relationtotalid='null'");
					ps.addBatch(" update prodrequisitiondetail p set p.relationtotalid=ifnull((select k.totalid from  prodrequisition_work_total k where k.worksheetid=p.worksheetid and p.itemid=k.itemid),'') where  p.worksheetid<>'' ");

				}

				if (version < 1.35 && newversion >= 1.35) {
					// 2020-11-16 支付退款 保存月表与年表 数据错了
					ps.addBatch("update customermonth c,s_company_config sc set c.rec_money=round(c.rec_this_money+c.rec_dis_money,sc.moneybit) where c.companyid=sc.company_id");
					ps.addBatch("update customermonth c,s_company_config sc set c.pay_dis_money=round(ifnull((select sum(if(ab.stype=4,-ab.rec_discount,ab.pay_discount)) as pay_dis from accountbill ab where ab.customerid = c.customerid and ab.status='1' and ab.stype in (2,3,4) and year(ab.operate_time)=c.syear and month(ab.operate_time)=c.smonth),0),sc.moneybit) where c.companyid=sc.company_id");
					ps.addBatch("update customermonth c,s_company_config sc set c.pay_money = round(c.pay_this_money+c.pay_dis_money,sc.moneybit)  where c.pay_this_money+c.pay_dis_money<>c.pay_money and c.companyid=sc.company_id");
					ps.addBatch("update customeryear cy,s_company_config sc set cy.pay_dis_money = round(ifnull((select sum(ab.pay_dis_money) as pay_dis from customermonth ab where ab.customerid = cy.customerid and ab.syear=cy.syear),0),sc.moneybit),cy.pay_money = round(cy.pay_this_money+cy.pay_dis_money,sc.moneybit)  where  cy.companyid=sc.company_id");

				}

				if (version < 1.36 && newversion >= 1.36) {
					// 景工定制功能sql ps.addBatch("");
					ps.addBatch("alter table storetemplate add stype int(11) not null default 1 comment '合同类型',add titlesize int(11) not null default 18 comment '标题字体大小',"
							+ "add gridsize int(11) not null default 14 comment '表格字体大小',add othersize int(11) not null default 15 comment '其他字体大小',"
							+ "add position int(11) not null default 1 comment '甲乙方位置', add Afax varchar(50) not null default '' comment '传真'");

					ps.addBatch("alter table storecontract add stype int(11) not null default 0 comment '合同类型',add Bfax varchar(50) not null default '' comment '乙方传真',add Afax varchar(50) not null default '' comment '传真'");
					ps.addBatch("alter table outsourcing add contractid varchar(36) not null default '' comment '采购合同id',add contractorderid varchar(50) not null default '' comment '采购合同'");

					ps.addBatch("create table purchase (`purchaseid` varchar(36) not null comment '编号',"
							+ "`bill_type` varchar(5) not null default '' comment '单据类型',`originalbill` varchar(50) not null default '' comment '原单号',"
							+ "`companyid` varchar(36) not null default '' comment '企业编号',`orderid` varchar(36) not null default '' comment '单据编号',"
							+ "`operate_time` date null default null comment '申请日期',`plandate` date null default null comment '计划到货日期',"
							+ "`operate_by` varchar(50) not null default '' comment '申请人',`count` double not null default 0 comment '总数量',"
							+ "`ordercount` double not null default 0 comment '已下单数量',`remark` varchar(200) not null default '' comment '备注',"
							+ "`status` varchar(1) not null default '' comment '状态',`orderstatus` varchar(1) not null default '' comment '下单状态',"
							+ "`printing` int(11) not null default 0 comment '打印次数',`outexcel` int(11) not null default 0 comment '导出次数',"
							+ "`create_id` varchar(36) not null default '' comment '创建人ID',`create_by` varchar(50) not null default '' comment '创建人',"
							+ "`create_time` datetime null default null comment '创建时间',`audit_id` varchar(36) not null default '' comment '审核人ID',"
							+ "`audit_by` varchar(50) not null default '' comment '审核人',`audit_time` datetime null default null comment '审核时间',"
							+ "`update_id` varchar(36) not null default '' comment '更新人ID',`update_by` varchar(50) not null default '' comment '更新人',"
							+ "`update_time` datetime null default null comment '更新时间',`iproperty` varchar(100) not null default '' comment '属性列表',"
							+ "primary key (`purchaseid`),index `companyid` (`companyid`),index `orderid` (`orderid`),index `operate_time` (`operate_time`),index `create_id` (`create_id`))");

					ps.addBatch("create table purchasedetail (`detailid` varchar(36) not null comment '编号',`purchaseid` varchar(36) not null comment '采购申请ID',"
							+ "`originalbill` varchar(50) not null default '' comment '原单号',`goods_number` int(11) null default null comment '序号',"
							+ "`companyid` varchar(36) not null default '' comment '企业编号',`orderid` varchar(36) not null default '' comment '单据编号',"
							+ "`operate_time` date null default null comment '申请日期',`plandate` date null default null comment '计划到货日期',"
							+ "`operate_by` varchar(50) not null default '' comment '申请人',`itemid` varchar(36) not null default '' comment '商品编号',"
							+ "`batchno` varchar(50) not null default '' comment '批号',`count` double not null default 0 comment '总数量',"
							+ "`ordercount` double not null default 0 comment '已下单数量',`stype` varchar(5) not null default '' comment '类型',"
							+ "`remark` varchar(200) not null default '' comment '备注',`status` varchar(1) not null default '' comment '状态',"
							+ "`orderstatus` varchar(1) not null default '' comment '下单状态',`printing` int(11) not null default 0 comment '打印次数',"
							+ "`outexcel` int(11) not null default 0 comment '导出次数',`create_id` varchar(36) not null default '' comment '创建人ID',"
							+ "`create_by` varchar(50) not null default '' comment '创建人',`create_time` datetime null default null comment '创建时间',"
							+ "`audit_id` varchar(36) not null default '' comment '审核人ID',`audit_by` varchar(50) not null default '' comment '审核人',"
							+ "`audit_time` datetime null default null comment '审核时间',`update_id` varchar(36) not null default '' comment '更新人ID',"
							+ "`update_by` varchar(50) not null default '' comment '更新人',`update_time` datetime null default null comment '更新时间',"
							+ "primary key (`detailid`),index `purchaseid` (`purchaseid`),index `companyid` (`companyid`),index `operate_time` (`operate_time`),index `orderid` (`orderid`),index `itemid` (`itemid`),index `create_id` (`create_id`))");

					ps.addBatch("alter table purchaseorderdetail add relationdetailid varchar(36) not null default '' comment '关联采购申请明细id',"
							+ "add relationorderid varchar(36) not null default '' comment '关联采购申请',add relationmainid varchar(36) not null default '' comment '关联采购申请主表id'");

					// ps.addBatch("alter table storeout add contractno varchar(50) not null default '' comment '合同号码'");

					// ps.addBatch("create or replace view storeout_view as  select so.*,c.customercode,c.customername,ifnull(sh.housecode,'') as housecode,ifnull(sh.housename,'') as housename,s.staffcode,s.staffname from storeout so left join customer c on so.customerid=c.customerid left join  storehouse sh on so.houseid=sh.houseid left join staffinfo s on so.operate_by=s.staffid");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C9164C86DE900001E2C512F919A031A0', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 15, '打印合同', 'outsourcingdata:printcontract', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-06 12:10:48', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-06 12:13:06', 1),"
							+ "('C9164C9ECAF00001B4C5146EF5B01855', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 13, '查看合同', 'outsourcingdata:showcontract', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-06 12:12:26', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-06 12:12:26', 1),"
							+ "('C9164CA421A00001325B100A1AD01B5F', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 14, '保存合同', 'outsourcingdata:contract', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-06 12:12:48', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-06 12:12:48', 1),"
							+ "('C91750D402D00001E14741A2C0701F1C', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 13, '导入单据', 'purchasedata:importdata', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-09 15:59:54', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-19 09:28:25', 1),"
							+ "('C91A72602E4000019AB11D571E315A30', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 12, '修改下单状态', 'purchasedata:orderstatus', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-19 09:27:57', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-10-19 09:27:57', 1)");

					ps.addBatch("create or replace view purchasedetail_all_view as select  sd.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,s.staffcode,s.staffname from purchasedetail sd left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid");

					ps.addBatch("create or replace view purchasedetail_item_view as select im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname,sd.* from purchasedetail sd,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid   and im.companyid=sd.companyid");
					ps.addBatch("create or replace view purchaseorderdetail_all_view as select  sd.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,c.customercode,c.customername,sh.housecode,sh.housename,s.staffcode,s.staffname,p.contractid,p.contractorderid,p.stockstatus from purchaseorderdetail sd left join customer c on sd.customerid=c.customerid left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid,purchaseorder p where  sd.purchaseorderid=p.purchaseorderid and im.itemid=sd.itemid and im.companyid=sd.companyid");
					ps.addBatch("create or replace view purchaseorderdetail_item_view as select im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname,sd.* from purchaseorderdetail sd,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid   and im.companyid=sd.companyid");

					ps.addBatch("alter table mrpconfig add `purchaseapp_na` INT(11) NULL DEFAULT 1 COMMENT '采购申请未审+',add `purchaseapp_a` INT(11) NULL DEFAULT 1 COMMENT '采购申请已审+'");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C87CC04BB7B00001559C16DE153061DA', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 1, '查看', 'purchasedata:read', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:49:40', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:50:20', 2),('C87CC0619F300001CE6DF75077C0C85A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 2, '新增', 'purchasedata:new', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:51:10', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:51:39', 1),('C87CC061FEA00001503B1AF0230B1EFA', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 3, '修改', 'purchasedata:edit', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:51:12', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:53:17', 1),('C87CC062312000015C841B9319007CDA', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 4, '删除', 'purchasedata:del', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:51:13', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:53:27', 1),('C87CC0624120000189A11500D03B6F0A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 5, '详情', 'purchasedata:detail', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:51:13', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:54:57', 1),('C87CC062512000015DA3A35D7FF01F8A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 6, '审核', 'purchasedata:audit', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:51:13', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:55:15', 1),('C87CC09DFDA000011DAE11B913D0AE6A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 7, '复制', 'purchasedata:copynew', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:55:17', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:55:36', 1),"
							+ "('C87CC0A7F1A00001EA414DF048F11F9A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 8, '作废', 'purchasedata:status', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:55:58', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:56:12', 1),('C87CC0AC90B0000114FE1CF01D50D97A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 9, '导出汇总', 'purchasedata:exporttotal', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:56:17', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:56:39', 1),('C87CC0ACBB300001CC9D19001B801A7A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 10, '导出明细', 'purchasedata:exportdetail', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:56:18', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 10:56:55', 1),"
							+ "('C87CE9A5BE900001AE27CB0016F8169A', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 11, '打印', 'purchasedata:print', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 22:52:21', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2019-06-17 22:52:47', 1),"
							+ "('C9205333F07000018A69154095BA15B2', 3, 40, '报表模块', 'reportmodel', 9, '扫码查单', 'scanseeorderdata', 1, 1, '查看', 'scanseeorderdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-06 15:46:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-06 16:13:51', 1),"// 二维码扫码
							+ "('C9214757A69000015D8310B01FF0B990', 3, 1, 'App端', 'appdata', 10, '扫码查单', 'appscanseeorderdata', 1, 1, '查看', 'appscanseeorderdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-09 14:53:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-09 14:54:53', 1),"// 二维码扫码
							+ "('C921A1560DC0000147DC13907D008850', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 15, '保存合同', 'salesorderdata:contract', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-10 17:06:07', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-10 17:06:41', 1),"// 销售合同
							+ " ('C921A16067D00001D98450DC1FE6B640', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 16, '查看合同', 'salesorderdata:showcontract', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-10 17:06:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-10 17:07:22', 1),"// 销售合同
							+ "('C921A168F5B0000171511DE01A532EF0', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 17, '打印合同', 'salesorderdata:printcontract', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-10 17:07:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-10 17:07:48', 1)");// 销售合同

					ps.addBatch("create or replace view outsourcing_view  as select os.*,c.customercode,c.customername,si.staffcode,si.staffname from outsourcing os left join customer c on os.customerid=c.customerid left join staffinfo si on os.operate_by=si.staffid");

					ps.addBatch("create or replace view purchase_view as select p.*,s.staffcode,s.staffname from purchase p left join staffinfo s on p.operate_by=s.staffid");

					ps.addBatch("update s_permission set parentname='合同模块',parentvalue='contractmodel',fseq='10',functionname='合同模板管理',functionvalue='contractmanage',fvalue='contractmanage:read',pseq=15 where parentvalue='storeinmodel' and functionvalue='storecontractdata' and seq=1");

					ps.addBatch("update s_permission set parentname='合同模块',parentvalue='contractmodel',fseq='10',functionname='合同模板管理',functionvalue='contractmanage',fvalue='contractmanage:new',pseq=15 where parentvalue='storeinmodel' and functionvalue='storecontractdata' and seq=2");

					ps.addBatch("update s_permission set parentname='合同模块',parentvalue='contractmodel',fseq='10',functionname='合同模板管理',functionvalue='contractmanage',fvalue='contractmanage:edit',pseq=15 where parentvalue='storeinmodel' and functionvalue='storecontractdata' and seq=3");

					ps.addBatch("update s_permission set parentname='合同模块',parentvalue='contractmodel',fseq='10',functionname='合同模板管理',functionvalue='contractmanage',fvalue='contractmanage:copynew',pseq=15 where parentvalue='storeinmodel' and functionvalue='storecontractdata' and seq=4 ");

					ps.addBatch("update s_permission set parentname='合同模块',parentvalue='contractmodel',fseq='10',functionname='合同模板管理',functionvalue='contractmanage',fvalue='contractmanage:status',pseq=15 where parentvalue='storeinmodel' and functionvalue='storecontractdata' and seq=5");

					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `fax` VARCHAR(50) NULL DEFAULT '' COMMENT '传真' AFTER `password`");

					ps.addBatch("ALTER TABLE s_company_config ADD showqrcode int  NOT NULL default 0 COMMENT '单据打印不显示二维码'");

					ps.addBatch("create or replace view s_companyconfig as select c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("alter table salesorder add contractid varchar(36) not null default '' comment '销售合同id', add contractorderid varchar(50) not null default '' comment '销售合同'");
					ps.addBatch("alter table salesorder add index contractid(contractid)");

					ps.addBatch("create or replace view salesorder_view as select so.*,c.customercode,c.customername,c.role,sh.housecode,sh.housename,s.staffcode,s.staffname from salesorder so left join customer c on so.customerid=c.customerid left join  storehouse sh on so.houseid=sh.houseid left join staffinfo s on so.operate_by=s.staffid");

					ps.addBatch("create or replace view salesorderdetail_all_view as select sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,ifnull(ic.classname,'') as classname ,c.customercode,c.customername,c.role,sh.housecode,sh.housename,s.staffcode,s.staffname from salesorderdetail sd left join customer c on sd.customerid=c.customerid left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid");
					ps.addBatch("create or replace view salesorderdetail_item_view as select  im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,ifnull(ic.classname,'') as classname,sd.* from salesorderdetail sd,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid   and im.companyid=sd.companyid");

					ps.addBatch("ALTER TABLE `prodrequisition_work_total` ADD COLUMN `salesdetailid` VARCHAR(36) NULL DEFAULT '' COMMENT '销售订单明细ID' AFTER `salesorderid`,ADD INDEX `salesdetailid` (`salesdetailid`)");
					ps.addBatch("update prodrequisition_work_total pt,t_order t set pt.salesdetailid=t.salesorderdetailid where pt.worksheetid=t.id and pt.salesorderid<>''");

				}

				if (version < 1.37 && newversion >= 1.37) {

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_user_deposit` ( `id` varchar(36) NOT NULL COMMENT '主键',`userid` varchar(50) DEFAULT NULL COMMENT '业务ID',"
							+ "`depositdate` date DEFAULT NULL COMMENT '充值日期',  `money` double DEFAULT NULL COMMENT '充值金额',`remark` varchar(200) DEFAULT NULL COMMENT '备注',"
							+ "`create_id` varchar(36) DEFAULT NULL COMMENT '登记人ID',`create_by` varchar(36) DEFAULT NULL COMMENT '登记人',`create_date` datetime DEFAULT NULL COMMENT '登记日期',"
							+ "PRIMARY KEY (`id`), KEY `userid` (`userid`),  KEY `depositdate` (`depositdate`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务充值表'");

					ps.addBatch("ALTER TABLE `s_userinfo`  ADD  `balance` double  DEFAULT 0 COMMENT '余额', ADD  `total` double  DEFAULT 0 COMMENT '总额', ADD  `usedmoney` double  DEFAULT 0 COMMENT '已用金额'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES"
							+ "('C925C5DABB90000116CEC2601AE8F6E0', 1, 100, '系统管理', 'systemset', 31, '用户业务配置', 'userfeeparam', 1, 4, '充值', 'userfeeparam:deposit', NULL, 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-11-23 14:00:01', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-11-23 14:00:44', 1)");
					ps.addBatch("update s_permission set functionname='用户业务配置' where functionname='用户业务费用'");
					// 2020-11-25 修复排产单变更数量，需领料数比例变更错误问题
					ps.addBatch("update  prodrequisition_work_total p,t_order t,s_company_config s set  p.needcount=round(p.unitcount*t.order_count,s.countbit)  where p.worksheetid=t.id and p.companyid=s.company_id");
				}

				if (version < 1.38 && newversion >= 1.38) {
					// 2020-12-02 工单管理明细增加查看物料BOM
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C928A292DC200001C2321DC512B31673', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 13, '查看物料BOM', 'orderdata:showneed', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-12-02 11:25:13', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-12-02 11:25:33', 1)");

					ps.addBatch("update purchaseorder p set p.stockstatus = '1' where (p.stockstatus='0' or p.stockstatus='-1')  and p.status='1' and (select count(pd.detailid) from purchaseorderdetail pd where pd.purchaseorderid=p.purchaseorderid"
							+ " and pd.status='1')=(select count(pd.detailid) from purchaseorderdetail pd where pd.purchaseorderid=p.purchaseorderid and pd.count<=pd.incount and pd.status='1')");

					// 2020-12-4 修复出纳结算对帐年月报表保存总金额问题
					ps.addBatch("update accountmonth am ,(select f.companyid,f.accountid,f.syear,f.smonth,sum(f.rec_money) as in_money,sum(f.pay_money) as out_money  from (select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,ab.rec_money,ab.pay_money from accountbill ab where ab.status='1' "
							+ " union all select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,ab.rec_money,ab.pay_money from dayinout ab where ab.status='1' ) f group by f.companyid,f.accountid,f.syear,f.smonth ) k,s_company_config sc set am.in_money=round(k.in_money,sc.moneybit),am.out_money=round(k.out_money,sc.moneybit),am.money=round(k.in_money-k.out_money,sc.moneybit)  where k.companyid=sc.company_id and k.companyid=am.companyid and k.accountid=am.accountid and k.syear=am.syear and k.smonth=am.smonth ");

					ps.addBatch("update accountyear am ,(select f.companyid,f.accountid,f.syear,sum(f.rec_money) as in_money,sum(f.pay_money) as out_money  from (select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,ab.rec_money,ab.pay_money from accountbill ab where ab.status='1' "
							+ " union all select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,ab.rec_money,ab.pay_money from dayinout ab where ab.status='1' ) f group by f.companyid,f.accountid,f.syear )k ,s_company_config sc set am.in_money=round(k.in_money,sc.moneybit),am.out_money=round(k.out_money,sc.moneybit),am.money=round(k.in_money-k.out_money,sc.moneybit)  where k.companyid=am.companyid and k.accountid=am.accountid and k.syear=am.syear");

					ps.addBatch("delete from prodrequisition_work_total where length(itemid)<5");
				}

				if (version < 1.39 && newversion >= 1.39) {
					ps.addBatch("create table stepnewquality ( "
							+ "`qualityid` varchar(36) not null comment '项目编号' , `companyid` varchar(36) not null default '' comment '企业编号',`stepnewid` varchar(36) not null default '' comment '工序id',"
							+ "`qualityno` int null default 0 comment '显示顺序', `title` varchar(100) not null default '' comment '质检项目',`type` int not null default 1 comment '项目类型',"
							+ "`options` varchar(300) not null default '' comment '项目选项', `optionlimit` int not null default 0 comment '多选限制',`optioninput` varchar(50) not null default '' comment '增加最后选项可填备注',"
							+ "`stype` int not null default 2 comment '是否必填', `fstatus` int not null default 1 comment '状态',`create_id` varchar(36) not null default '' comment '创建人ID',"
							+ "`create_by` varchar(50) not null default '' comment '创建人', `create_time` datetime null default null comment '创建时间',`update_id` varchar(36) not null default '' comment '更新人ID',"
							+ "`update_by` varchar(50) not null default '' comment '更新人', `update_time` datetime null default null comment '更新时间',"
							+ " primary key (`qualityid`), index `companyid` (`companyid`), index `stepnewid` (`stepnewid`) )comment='工序质检项',collate='utf8_general_ci',engine=InnoDB");

					ps.addBatch("create table stepnewanswer (" + "`answerid` varchar(36) not null comment '回答编号', `companyid` varchar(36) not null default '' comment '企业编号',"
							+ "`qualityid` varchar(36) not null default '' comment '项目编号', `order_progressid` varchar(36) not null default '' comment '报工id',"
							+ "`stepnewid` varchar(36) not null default '' comment '工序id', `answer` varchar(300) not null default '' comment '回答内容',"
							+ "`answerinput` varchar(100) not null default '' comment '回答备注', `detail_id` varchar(36) not null default '' comment '工单明细id',"
							+ "`step_id` varchar(36) not null default '' comment '工序id', `create_id` varchar(36) not null default '' comment '创建人ID',"
							+ "`create_by` varchar(50) not null default '' comment '创建人', `create_time` datetime null default null comment '创建时间',"
							+ "`fstatus` int not null default 1 comment '状态', primary key (`answerid`), index `companyid` (`companyid`),"
							+ "unique index `qualityid` (`qualityid`,`order_progressid`),index `order_progressid` (`order_progressid`),index `stepnewid` (`stepnewid`)"
							+ ") comment ='质检回答表', COLLATE='utf8_general_ci', ENGINE=InnoDB");
					ps.addBatch("alter table t_stepnew add quality int not null default 0 comment '质检项'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C92296F563900001212C18D0587A10F7', 3, 50, '基础模块', 'basicset', 85, '工序管理', 'new_stepmanage', 1, 6, '质检配置', 'new_stepmanage:quality', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-13 16:38:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-11-13 16:38:58', 1),"
							+ "('C92AF35FEC400001AB8B7970184012E2', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 29, '批量更新商品信息', 'iteminfodata:updateitem', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-09 16:05:10', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-09 16:05:45', 1),"
							+ "('C92B96EB44200001FBEE6BF710B925C0', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 14, '批量修改备注', 'orderdata:commentchangebatch', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-11 15:43:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-11 15:43:36', 1),"
							+ "('C92CDC5A5E100001516215A05EF18F50', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 10, '箱单打印', 'storeoutdata:boxprint', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-15 14:30:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-15 14:31:03', 1),"
							+ "('C92FB78AC7900001225212C01B35C430', 3, 40, '报表模块', 'reportmodel', 70, '库存进出汇总', 'storeinoutallreportdata', 1, 2, '导出', 'storeinoutallreportdata:exportinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-24 11:29:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-24 11:29:34', 1),"
							+ "('C926BDA12EF00001E443E670D400191F', 3, 3, '看板管理', 'boardset', 50, '订单进度看板', 'boardsalesorder', 1, 1, '查看', 'boardsalesorder:read', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-11-26 14:10:12', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2020-11-26 14:10:46', 1)");

					ps.addBatch("alter table t_detail_progress add `order_progressid` varchar(36) DEFAULT '' COMMENT '报工ID'");
					ps.addBatch("create table itemfile ("
							+ "`fileId` varchar(36) not null comment '商品文件id', `companyid` varchar(36) null default null comment '公司id',`filepath` varchar(200) null default null comment '文件路径',"
							+ "`filename` varchar(200) null default null comment '文件名称', `create_id` varchar(36) null default null comment '创建人ID',`create_by` varchar(50) null default null comment '创建人',`create_time` datetime null default null comment '创建时间',"
							+ " primary key (`fileId`), KEY `companyid` (`companyid`))  comment='商品更新文件',collate='utf8_general_ci',engine=InnoDB");

					ps.addBatch("alter table iteminfo add package_count double not null default 0 comment '包装数量'");

					ps.addBatch("create or replace view storeoutdetail_item_view as select  im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.package_count,ifnull(ic.classname,'') as classname,sd.*,sh.housecode,sh.housename from  storeoutdetail sd left join storehouse sh on sd.houseid=sh.houseid left join iteminfo im on im.itemid=sd.itemid and im.companyid=sd.companyid left join itemclass ic on im.classid=ic.classid");

					ps.addBatch("update s_permission set pseq='3',parentname='看板管理',parentvalue='boardset', fseq = 10,functionname='生产日报表',functionvalue='daysheetboard',seq=1,fname='查看',fvalue='daysheetboard:read' where id = 'C8696BFED140000181E31CCF145014F6'");
					ps.addBatch("update s_permission set pseq='3',parentname='看板管理',parentvalue='boardset', fseq = 20,functionname='工单进度看板',functionvalue='orderprogressboard',seq=1,fname='查看',fvalue='orderprogressboard:read' where id = 'C8696CC83F40000188143B30320017AC'");
					ps.addBatch("update s_permission set pseq='3',parentname='看板管理',parentvalue='boardset',fseq = 30,functionname='工艺进度看板',functionvalue='stepprogressboard',seq=1,fname='查看',fvalue='stepprogressboard:read' where id = 'C8696D2BD0B00001DE8E3870DF40DCE0'");
					ps.addBatch("update s_permission set pseq='3',parentname='看板管理',parentvalue='boardset', fseq = 40,functionname='工艺工单看板',functionvalue='steporderboard',seq=1,fname='查看',fvalue='steporderboard:read' where id = 'C8696D3254400001FA982D1F1D607050'");

					ps.addBatch("ALTER TABLE `t_userfee_param` ADD COLUMN `timeval` INT(5) NULL DEFAULT '1' COMMENT '时间量' AFTER `amount`");

					// ps.addBatch("ALTER TABLE `prodrequisition_work_total`  ADD COLUMN `salesdetailid` VARCHAR(36) NULL DEFAULT '' COMMENT '销售订单明细ID' AFTER `salesorderid`,ADD INDEX `salesdetailid` (`salesdetailid`)");
					// ps.addBatch("update prodrequisition_work_total pt,t_order t set pt.salesdetailid=t.salesorderdetailid where pt.worksheetid=t.id and pt.salesorderid<>''");

					ps.addBatch("ALTER TABLE `customer` CHANGE COLUMN `mcode` `mcode` VARCHAR(100) NULL DEFAULT NULL COMMENT '助记码' AFTER `typeid`");
				}

				if (version < 1.40 && newversion >= 1.40) {
					ps.addBatch("CREATE TABLE `t_customer_userid` ( `id` VARCHAR(36) NULL DEFAULT NULL COMMENT '主键', `companyid` VARCHAR(50) NULL DEFAULT NULL COMMENT '组织编号',"
							+ "`customerid` VARCHAR(50) NULL DEFAULT NULL COMMENT '往来单位编号', `userid` VARCHAR(50) NULL DEFAULT NULL COMMENT '用户编号',"
							+ "`create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建人',"
							+ "`create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', UNIQUE INDEX `customerid_userid` (`customerid`, `userid`),"
							+ "INDEX `companyid` (`companyid`), INDEX `customerid` (`customerid`), INDEX `userid` (`userid`) )COLLATE='utf8_general_ci'ENGINE=InnoDB");

					ps.addBatch("CREATE TABLE `t_storehouse_userid` ( `id` VARCHAR(36) NULL DEFAULT NULL COMMENT '主键', `companyid` VARCHAR(50) NULL DEFAULT NULL COMMENT '组织编号',"
							+ "`houseid` VARCHAR(50) NULL DEFAULT NULL COMMENT '仓库编号', `userid` VARCHAR(50) NULL DEFAULT NULL COMMENT '用户编号',`create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID',"
							+ "`create_by` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期',UNIQUE INDEX `houseid_userid` (`houseid`, `userid`),"
							+ "INDEX `companyid` (`companyid`), INDEX `houseid` (`houseid`), INDEX `userid` (`userid`) ) COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("ALTER TABLE `customer` ADD COLUMN `usercount` INT NULL  DEFAULT '0' COMMENT '授权用户数'");
					ps.addBatch("ALTER TABLE `storehouse` ADD COLUMN `usercount` INT NULL  DEFAULT '0' COMMENT '授权用户数'");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `unitstate1` INT(1) NULL DEFAULT '0' COMMENT '辅助单位1状态' ,"
							+ "ADD COLUMN `unitstate2` INT(1) NULL DEFAULT '0' COMMENT '辅助单位2状态' , ADD COLUMN `unitstate3` INT(1) NULL DEFAULT '0' COMMENT '辅助单位3状态' ,"
							+ "ADD COLUMN `unitname1` VARCHAR(20) NULL DEFAULT '辅助单位1' COMMENT '辅助单位1名称' , ADD COLUMN `unitname2` VARCHAR(20) NULL DEFAULT '辅助单位2' COMMENT '辅助单位2名称' ,"
							+ "ADD COLUMN `unitname3` VARCHAR(20) NULL DEFAULT '辅助单位3' COMMENT '辅助单位3名称'");

					ps.addBatch("ALTER TABLE `iteminfo`  ADD COLUMN `unitstate1` INT(1) NULL DEFAULT '0' COMMENT '辅助单位1类型' ,"
							+ "ADD COLUMN `unitstate2` INT(1) NULL DEFAULT '0' COMMENT '辅助单位2类型' , ADD COLUMN `unitstate3` INT(1) NULL DEFAULT '0' COMMENT '辅助单位3类型' ,"
							+ "ADD COLUMN `unitset1` VARCHAR(50) NULL DEFAULT '' COMMENT '辅助单位1设置' , ADD COLUMN `unitset2` VARCHAR(50) NULL DEFAULT '' COMMENT '辅助单位2设置' ,"
							+ "ADD COLUMN `unitset3` VARCHAR(50) NULL DEFAULT '' COMMENT '辅助单位3设置'");

					ps.addBatch("create or replace view s_companyconfig as select c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1' ");

					ps.addBatch("create or replace view itembegindetail_all_view as SELECT sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,sh.housecode,sh.housename,s.staffcode,s.staffname from itembegindetail sd left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view item_class_view as SELECT im.*,ifnull(cs.classname,'') as classname ,ifnull(tc.class_name,'') as class_name from iteminfo im left join itemclass cs on im.classid=cs.classid left join t_step_class tc on im.class_id=tc.id ");

					ps.addBatch("create or replace view purchasedetail_all_view as select  sd.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,im.inprice,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,s.staffcode,s.staffname from purchasedetail sd left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view purchaseorderdetail_all_view as select  sd.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,c.customercode,c.customername,sh.housecode,sh.housename,s.staffcode,s.staffname,p.contractid,p.contractorderid,p.stockstatus from purchaseorderdetail sd left join customer c on sd.customerid=c.customerid left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid,purchaseorder p where  sd.purchaseorderid=p.purchaseorderid and im.itemid=sd.itemid and im.companyid=sd.companyid");

					ps.addBatch("create or replace view storeindetail_all_view as select sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,c.customercode,c.customername,sh.housecode,sh.housename,s.staffcode,s.staffname from storeindetail sd left join customer c on sd.customerid=c.customerid left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view item_batchno_stock as select i.*,ifnull(cs.classname,'') as classname ,s.houseid ,sh.housecode,sh.housename,s.batchno,ifnull(s.count,0) as count,ifnull(s.money,0) as money,ifnull(s.newcostprice,0) as newcostprice,ifnull(s.checkout_count,0) as checkout_count  from iteminfo i left join itemclass cs on i.classid=cs.classid ,stock s left join storehouse sh on  s.houseid=sh.houseid where i.itemid = s.itemid ");

					ps.addBatch("create or replace view item_class_view as SELECT im.*,ifnull(cs.classname,'') as classname ,ifnull(tc.class_name,'') as class_name from iteminfo im left join itemclass cs on im.classid=cs.classid left join t_step_class tc on im.class_id=tc.id ");

					ps.addBatch("create or replace view salesorderdetail_all_view as select sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,c.customercode,c.customername,c.role,sh.housecode,sh.housename,s.staffcode,s.staffname from salesorderdetail sd left join customer c on sd.customerid=c.customerid left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid");

					ps.addBatch("create or replace view storeoutdetail_all_view as select   sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname,c.customercode,c.customername,sh.housecode,sh.housename,s.staffcode,s.staffname from  storeoutdetail sd left join customer c on sd.customerid=c.customerid left join  storehouse sh on sd.houseid=sh.houseid left join staffinfo s on sd.operate_by=s.staffid ,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid  and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view t_order_all_view as select   sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname,c.customercode,c.customername,s.staffcode,s.staffname,sl.scheduledcount,sl.schedulcount,sl.count from  t_order sd left join salesorderdetail sl on sd.salesorderdetailid=sl.detailid left join customer c on sd.customer_id=c.customerid left join staffinfo s on sd.operate_by=s.staffid ,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid  and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view t_order_detail_item_view as  select  tod.id,tod.fstatus,tod.companyid,tod.order_id,tod.class_id,tod.item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.scheduletype,tod.printing,tod.billno,tod.batchno,tod.must_item_count,tod.max_item_count,im.codeid,im.itemname,"
							+ "im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,im.splits,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,ifnull(ic.classname,'') as classname "
							+ "from t_order_detail tod left join t_step_class tsc on tod.class_id=tsc.id left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid ");

					ps.addBatch("create or replace view t_order_detail_item_all_view as  select tod.id as did,tod.fstatus,tod.companyid,td.id as orderid,td.order_id,tod.class_id,tod.item_count,tod.max_item_count,tod.must_item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.printing,tod.billno,tod.batchno,tod.scheduletype,im.codeid,im.itemname,"
							+ "im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,td.iproperty,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,td.originalbill,td.operate_by,td.order_remark,td.update_by ,im.outprice5,im.splits,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,td.order_time,td.finish_time,td.id,ifnull(ic.classname,'') as classname,cm.customercode,cm.customername,td.create_id,td.create_by,td.create_date,td.audit_by,td.audit_date "
							+ "from t_order_detail tod  left join t_step_class tsc on tod.class_id=tsc.id left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid left join t_order td on tod.order_id=td.id left join customer cm on td.customer_id=cm.customerid ");

					ps.addBatch("create or replace view scheduleorderdetail_all_view as  select  td.*,cm.customercode,cm.customername,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,sc.class_code,sc.class_name,ic.classname,si.staffcode,si.staffname "
							+ "from t_order td left join customer cm on td.customer_id=cm.customerid left join staffinfo si on td.operate_by=si.staffid,"
							+ "iteminfo im left join t_step_class sc on im.class_id=sc.id left join itemclass ic on im.classid=ic.classid   where td.itemid=im.itemid ");

					ps.addBatch("create or replace view outsourcingdetail_all_view as  select osd.*,c.customercode,c.customername,si.staffcode,si.staffname,ifnull(sod.hadoutsourcing,0) as hadoutsourcing,ifnull(sod.outsourcingcount,0) as outsourcingcount,ifnull(sod.outsourcingin,0) as outsourcingin,ifnull(sod.count,0) as ordercount,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,oc.iproperty,ifnull(ic.classname,'')as classname "
							+ "from outsourcingdetail osd left join salesorderdetail sod on osd.relationdetailid=sod.detailid left join outsourcing oc on osd.outsourcingid=oc.outsourcingid left join customer c on osd.customerid=c.customerid left join staffinfo si on osd.operate_by=si.staffid, iteminfo im left join itemclass ic on im.classid=ic.classid where osd.itemid=im.itemid and osd.companyid=im.companyid ");

					ps.addBatch("create or replace view processinoutdetail_item_view as  select im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,o.*,sh.housecode,sh.housename from processinoutdetail o left join storehouse sh on o.houseid=sh.houseid, iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=o.itemid and im.companyid=o.companyid ");

					ps.addBatch("create or replace view processinoutdetail_all_view as  select o.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,im.property1,im.property2,im.property3,im.property4,im.property5,ifnull(ic.classname,'') as classname ,c.customercode,c.customername,sh.housecode,sh.housename,s.staffcode,s.staffname from processinoutdetail o left join customer c on o.customerid=c.customerid left join  storehouse sh on o.houseid=sh.houseid left join staffinfo s on o.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=o.itemid and im.companyid=o.companyid ");

					ps.addBatch("create or replace view outsourcingindetail_all_view as  select osid.*,osd.count as ocount,osd.detailid as odetailid,osd.orderid as oorderid,osd.outsourcingid,c.customercode,c.customername,si.staffcode,si.staffname,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'')as classname,sh.housecode,sh.housename "
							+ "from outsourcingindetail osid left join customer c on osid.customerid=c.customerid left join staffinfo si on osid.operate_by=si.staffid left join outsourcingdetail osd on osd.detailid=osid.relationdetailid left join storehouse sh on osid.houseid = sh.houseid,"
							+ "iteminfo im left join itemclass ic on im.classid=ic.classid where osid.itemid=im.itemid and osid.companyid=im.companyid ");

					ps.addBatch("create or replace view prodrequisitiondetail_all_view as  select pr.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname,sh.housecode,sh.housename ,c.customercode,c.customername,s.staffcode,s.staffname from prodrequisitiondetail pr left join storehouse sh on pr.houseid=sh.houseid left join customer c on pr.customerid=c.customerid left join staffinfo s on pr.operate_by=s.staffid ,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=pr.itemid   and im.companyid=pr.companyid ");

					ps.addBatch("create or replace view prodrequisitiondetail_item_view as  select im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,"
							+ "im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname,sh.housecode,sh.housename,pr.* from prodrequisitiondetail pr left join storehouse sh  "
							+ "on pr.houseid=sh.houseid,iteminfo im  left join itemclass ic on im.classid=ic.classid where im.itemid=pr.itemid   and  im.companyid=pr.companyid ");

					ps.addBatch("create or replace view t_order_view as  select   td.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.barcode,im.unit,im.imgurl,im.splits,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname,ifnull(cm.customercode,'') as customercode,ifnull(cm.customername,'') as customername,si.staffcode,si.staffname "
							+ "from t_order td left join staffinfo si on td.operate_by=si.staffid left join customer cm on td.customer_id=cm.customerid, iteminfo im left join itemclass ic on im.classid=ic.classid where td.itemid = im.itemid order by order_status asc ");

					ps.addBatch("create or replace view otherinoutdetail_all_view as  select  o.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,c.customercode,c.customername,sh.housecode,sh.housename,s.staffcode,s.staffname from otherinoutdetail o left join customer c on o.customerid=c.customerid left join  storehouse sh on o.houseid=sh.houseid left join staffinfo s on o.operate_by=s.staffid , iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=o.itemid and im.companyid=o.companyid ");

					ps.addBatch("create or replace view prodstoragedetail_all_view as  SELECT  pd.*,ii.codeid,ii.itemname,ii.sformat,ii.mcode,ii.classid,ii.unit,ii.imgurl,ii.barcode,ii.property1,ii.property2,ii.property3,ii.property4,ii.property5,ii.unitstate1,ii.unitset1,ii.unitstate2,ii.unitset2,ii.unitstate3,ii.unitset3,ifnull(ic.classname,'') as classname,sh.housecode,sh.housename,si.staffcode,si.staffname "
							+ "from prodstoragedetail pd left join staffinfo si on pd.operate_by=si.staffid left join storehouse sh on pd.houseid=sh.houseid ,  iteminfo  ii left join itemclass ic  on ic.classid=ii.classid  where ii.itemid=pd.itemid and ii.companyid=pd.companyid");

					ps.addBatch("create or replace view storemovedetail_all_view as  select  sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname,osh.housecode as oldhousecode,osh.housename as oldhousename,nsh.housecode as newhousecode,nsh.housename as newhousename,s.staffcode,s.staffname from storemovedetail sd left join storehouse osh on sd.oldhouseid=osh.houseid left join storehouse nsh on sd.newhouseid=nsh.houseid left join staffinfo s on sd.operate_by=s.staffid,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid   and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view storecheckdetail_all_view as  select sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,sh.housecode,sh.housename,s.staffcode,s.staffname  from storecheckdetail sd left join storehouse sh on sd.houseid=sh.houseid left join staffinfo s on  sd.operate_by=s.staffid,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid  and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view itemsplits_view as  SELECT im.inprice,im.barcode,im.unit,im.codeid,im.imgurl,im.itemname,im.sformat,im.classid,ic.classname,im.status,im.property1,im.property2,im.property3,im.property4,im.property5,im.class_id,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ims.* from itemsplits ims,iteminfo im left join itemclass ic on im.classid=ic.classid where ims.itemid=im.itemid order by ims.combitemid asc,ims.number asc ");

					ps.addBatch("create or replace view splitsdetail_all_view as  select sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,h.housecode,h.housename ,s.staffcode,s.staffname from splitsdetail sd left join storehouse h on sd.houseid=h.houseid  left join  staffinfo s on sd.operate_by=s.staffid ,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid ");

					ps.addBatch("create or replace view reportloss_all_view as  select sd.*, im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,osh.housecode,osh.housename,s.staffcode,s.staffname from reportlossdetail sd left join storehouse osh on  sd.houseid=osh.houseid left join staffinfo s on sd.operate_by=s.staffid ,iteminfo im left join itemclass ic on im.classid=ic.classid where im.itemid=sd.itemid and im.companyid=sd.companyid ");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `versionchange` ( `id` varchar(36) NOT NULL COMMENT '主键', `version` double NOT NULL COMMENT '版本号',  `updatetime` datetime DEFAULT NULL COMMENT '更新时间',"
							+ "`content` text COMMENT '更新内容', `fstatus` int(11) NOT NULL DEFAULT '1' COMMENT '状态', `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',"
							+ "`create_date` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) DEFAULT '' COMMENT '更新人', `update_date` datetime DEFAULT NULL COMMENT '更新时间', UNIQUE KEY `id` (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='版版本更新记录表本更新记录表'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('C92C4A2F3390000174EC198CD0EC5580', 3, 50, '基础模块', 'basicset', 50, '仓库管理', 'housedata', 1, 7, '用户配置', 'housedata:userset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 19:50:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 19:51:24', 1),"
							+ "('C92C4A3917800001157F72201B601E8C', 3, 50, '基础模块', 'basicset', 50, '仓库管理', 'housedata', 1, 6, '批量用户配置', 'housedata:usersetbatch', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 19:51:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 19:51:09', 1),"
							+ "('C92C4AC28A100001146BAE8CC22A8F80', 3, 50, '基础模块', 'basicset', 50, '仓库管理', 'housedata', 1, 8, '保存用户配置', 'housedata:usersetsave', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:00:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:06:14', 1),"
							+ "('C92C4B37BE500001E3598AD01C94165D', 3, 50, '基础模块', 'basicset', 50, '仓库管理', 'housedata', 1, 9, '清空用户配置', 'housedata:usersetclear', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:08:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:11:17', 1),"
							+ "('C92C4B7426E0000143F189275A00A6A0', 3, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 19, '清空用户配置', 'customerdata:usersetclear', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:12:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:56', 1),"
							+ "('C92C4B7B0F500001D32CC60414C0BCE0', 3, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 16, '批量用户配置', 'customerdata:usersetbatch', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:08', 1),"
							+ "('C92C4B7E8AD00001841913D041E018E1', 3, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 17, '用户配置', 'customerdata:userset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:23', 1),"
							+ "('C92C4B8288D00001546E15001A117480', 3, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 18, '保存用户配置', 'customerdata:usersetsave', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:13:39', 1),"
							+ "('C92C4C6609D00001EE231800128D171B', 3, 100, '系统管理', 'systemset', 30, '用户管理', 'userdata', 1, 11, '仓库配置', 'userdata:houseset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:29:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:29:27', 1),"
							+ "('C92C4C6A38400001903CD9801B201A87', 3, 100, '系统管理', 'systemset', 30, '用户管理', 'userdata', 1, 12, '往来单位配置', 'userdata:customerset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:29:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2020-12-13 20:31:09', 1),"
							+ "('C93A2DBF8E7000017635AD301E502AE0', 1, 100, '系统管理', 'systemset', 5, '版本更新记录管理', 'versionchangedata', 1, 4, '编辑', 'versionchangedata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:34:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:36:14', 1),"
							+ "('C93A2DCD92100001504F12897F501869', 1, 100, '系统管理', 'systemset', 5, '版本更新记录管理', 'versionchangedata', 1, 1, '查看', 'versionchangedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:35:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:35:08', 1),"
							+ "('C93A2DD2A9900001758F1C31CE781522', 1, 100, '系统管理', 'systemset', 5, '版本更新记录管理', 'versionchangedata', 1, 2, '新增', 'versionchangedata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:35:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:35:29', 1),"
							+ "('C93A2DD8C4400001985D1D661B00AA10', 1, 100, '系统管理', 'systemset', 5, '版本更新记录管理', 'versionchangedata', 1, 3, '删除', 'versionchangedata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:35:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-25 23:35:54', 1)");

				}

				if (version < 1.41 && newversion >= 1.41) {
					ps.addBatch("ALTER TABLE `t_order_progress` ADD INDEX `create_date` (`create_date`), ADD INDEX `companyid` (`companyid`), ADD INDEX `order_id` (`order_id`), ADD INDEX `detail_id` (`detail_id`), ADD INDEX `step_id` (`step_id`), ADD INDEX `device_id` (`device_id`), ADD INDEX `workshop_id` (`workshop_id`), ADD INDEX `user_id` (`user_id`)");
					ps.addBatch("ALTER TABLE `t_step`  ADD INDEX `fclass_id` (`class_id`), ADD INDEX `fstepnewid` (`stepnewid`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_orderstep_staff` ( `id` varchar(36) NOT NULL COMMENT '主键',  `companyid` varchar(36) NOT NULL COMMENT '组织主键', "
							+ "`order_id` varchar(36) NOT NULL COMMENT '订单id', `detail_id` varchar(36) NOT NULL COMMENT '订单明细id',  `stepid` varchar(36) NOT NULL COMMENT '工序id', "
							+ " `staffid` varchar(36) NOT NULL COMMENT '员工id', `create_id` varchar(36) NOT NULL COMMENT '创建人id', `create_by` varchar(50) NOT NULL COMMENT '创建人',"
							+ " `create_date` datetime NOT NULL COMMENT '创建时间', PRIMARY KEY (`id`),"
							+ " KEY `companyid` (`companyid`),  KEY `order_id` (`order_id`),  KEY `detail_id` (`detail_id`), KEY `stepid` (`stepid`),KEY `staffid` (`staffid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_stepnew_staff` ( `id` varchar(36) NOT NULL COMMENT '主键', `companyid` varchar(36) NOT NULL COMMENT '组织主键',"
							+ " `stepnewid` varchar(36) NOT NULL COMMENT '工序编号', `staffid` varchar(36) NOT NULL COMMENT '员工id', `create_id` varchar(36) NOT NULL COMMENT '创建人ID',"
							+ " `create_by` varchar(50) NOT NULL COMMENT '创建人', `create_date` datetime NOT NULL COMMENT '创建日期', PRIMARY KEY (`id`),"
							+ "  KEY `companyid` (`companyid`),  KEY `stepnewid` (`stepnewid`),  KEY `staffid` (`staffid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("ALTER TABLE `t_progress`  ADD COLUMN `step_price` DOUBLE NULL DEFAULT '0' COMMENT '工序单价' ");
					ps.addBatch("update t_progress t set t.step_price=if(progress_count>0,(select top.price from (select tops.detail_id,tops.step_id,tops.price,tops.fstatus from t_order_progress tops order by tops.create_date desc) top where top.detail_id=t.detail_id and top.step_id=t.step_id  and top.fstatus=1 limit 1 ),(select ts.price from t_step ts where ts.id=t.step_id)) where t.step_price=0");
					ps.addBatch("ALTER TABLE `t_progress` ADD COLUMN `staffcount` INT NOT NULL DEFAULT '0' COMMENT '员工数' ");

					ps.addBatch("ALTER TABLE `t_step_class` ADD COLUMN `finishstep` VARCHAR(36) NOT NULL DEFAULT 'all' COMMENT '完结工序'");
					ps.addBatch("ALTER TABLE `t_order_detail` ADD COLUMN `p_finishstep` VARCHAR(36) NOT NULL DEFAULT 'all' COMMENT '完结工序'");

					ps.addBatch("ALTER TABLE s_company_config ADD setstepfinish int  NOT NULL default 0 COMMENT '显示完结工序设置',ADD COLUMN `stepbegin` INT(1) NULL DEFAULT '0' COMMENT '每道工序第一次报工可不填写数量或数量为0'");
					ps.addBatch("create or replace view s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("create or replace view t_order_view as select  td.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.barcode,im.unit,im.imgurl,im.splits,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,ifnull(ic.classname,'') as classname,ifnull(cm.customercode,'') as customercode,ifnull(cm.mcode,'') as cmcode,ifnull(cm.customername,'') as customername,si.staffcode,si.staffname"
							+ " from t_order td left join staffinfo si on td.operate_by=si.staffid left join customer cm on td.customer_id=cm.customerid, iteminfo im left join itemclass ic on im.classid=ic.classid where td.itemid = im.itemid order by order_status asc");

					ps.addBatch("create or replace view t_order_detail_item_all_view as  select tod.id as did,tod.fstatus,tod.companyid,tod.p_finishstep,td.id as orderid,td.order_id,tod.class_id,tod.item_count,tod.max_item_count,tod.must_item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.printing,tod.billno,tod.batchno,tod.scheduletype,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,td.iproperty,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,td.originalbill,td.operate_by,td.order_remark,td.update_by ,im.outprice5,im.splits,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,td.order_time,td.finish_time,td.id,ifnull(ic.classname,'') as classname,cm.customercode,ifnull(cm.mcode,'') as cmcode,cm.customername,td.create_id,td.create_by,td.create_date,td.audit_by,td.audit_date from t_order_detail tod  left join t_step_class tsc on tod.class_id=tsc.id left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid left join t_order td on tod.order_id=td.id left join customer cm on td.customer_id=cm.customerid  ");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C93BFCF463E00001F48219601E301B51', 3, 50, '基础模块', 'basicset', 85, '工序管理', 'new_stepmanage', 1, 6, '生产员工配置', 'new_stepmanage:newstep_staff', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-31 14:29:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-01-31 14:30:09', 1),"
							+ "('C93D9E333D50000164BBCAB018C04900', 3, 3, '看板管理', 'boardset', 60, '工序进度看板', 'stepboard', 1, 1, '查看', 'stepboard:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-05 16:01:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-05 16:01:40', 1),"
							+ "('C93D9E58796000014049749DAF655310', 3, 25, '生产管理', 'orderset', 35, '工序进度报表', 'orderstepdata', 1, 2, '导出数据', 'orderstepdata:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-05 16:03:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-05 16:05:51', 1),"
							+ "('C93D9E733750000193771CC438361EFE', 3, 25, '生产管理', 'orderset', 35, '工序进度报表', 'orderstepdata', 1, 1, '查看', 'orderstepdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-05 16:05:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-05 16:05:34', 1),"
							+ "('C943AC78E54000015CBC6FE05BA03380', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 18, '修改原单号', 'salesorderdata:changeoriginalbill', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-24 11:34:10', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-24 11:34:34', 1),"
							+ "('C943B9651E400001381A14E0BF70B2F0', 3, 25, '生产管理', 'orderset', 60, '工艺工单效率', 'summaryordersteptimedata', 1, 2, '导出数据', 'summaryordersteptimedata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-24 15:20:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-02-24 15:20:34', 1),"
							+ "('C943DAC4A7C00001A9D41BC0C81C6490', 3, 50, '基础模块', 'basicset', 85, '工序管理', 'new_stepmanage', 1, 9, '生产员工配置', 'new_stepmanage:newstep_staff', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2021-02-25 01:03:15', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2021-02-25 01:03:43', 1)");

					// 2021-03-11
					ps.addBatch("update iteminfo set codeid=replace(codeid,'\n',' '),itemname=replace(itemname,'\n',' '),sformat=replace(sformat,'\n',' ') where codeid like '%\n%' or itemname like '%\n%' or sformat like '%\n%'");

					ps.addBatch("delete from s_roles_permission where id in ('a5e8ff32725f416e92bec87588efec79','1d14db9eb4bb432781b732a676bd7cc9','2d1a11e707794c3eaf91e922ff8b3159','3c5409e7ee534401a49e7d881e724169')");
					ps.addBatch("INSERT INTO `s_roles_permission` (`id`, `roleid`, `functionid`, `create_id`, `create_by`, `create_date`) VALUES"
							+ "('a5e8ff32725f416e92bec87588efec79', 'C89C07871F8000012C3715468637ECF0', 'C891351A2A400001F2A716409A803A60', 'cf9455f732a740aa806d7704fb68aead', '后台管理员', now()),"
							+ "('1d14db9eb4bb432781b732a676bd7cc9', 'C89C07871F8000012C3715468637ECF0', 'C84D1E27839000013BBAA98B1DD01207', 'cf9455f732a740aa806d7704fb68aead', '后台管理员', now()),"
							+ "('2d1a11e707794c3eaf91e922ff8b3159', 'C89C07871F8000012C3715468637ECF0', 'C84D1E383B1000014C9F19D0162036E0', 'cf9455f732a740aa806d7704fb68aead', '后台管理员', now()),"
							+ "('3c5409e7ee534401a49e7d881e724169', 'C89C07871F8000012C3715468637ECF0', 'C84D1E444E20000161771F301A4A14D1', 'cf9455f732a740aa806d7704fb68aead', '后台管理员', now())");

				}

				if (version < 1.42 && newversion >= 1.42) {
					ps.addBatch("update s_permission set fstatus=2 where fvalue='orderdata:stepprintdetail'");
					ps.addBatch("create or replace view salesorder_view as select so.*,c.customercode,c.customername,c.customerphone,c.role,sh.housecode,sh.housename,s.staffcode,s.staffname from salesorder so left join customer c on so.customerid=c.customerid left join  storehouse sh on so.houseid=sh.houseid left join staffinfo s on so.operate_by=s.staffid ");
					ps.addBatch("create or replace view  purchaseorder_view as select po.*,c.customercode,c.customername,c.customerphone,sh.housecode,sh.housename,s.staffcode,s.staffname from purchaseorder po left join customer c on po.customerid=c.customerid left join  storehouse sh on po.houseid=sh.houseid left join staffinfo s on po.operate_by=s.staffid ");

					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `checkrecommend` VARCHAR(20) NULL DEFAULT 'molisoft' COMMENT '默认推荐码',ADD COLUMN `prefessions` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '行业数据'");
					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `profession` VARCHAR(50) NULL DEFAULT '' COMMENT '所属行业'");

					ps.addBatch("create or replace view s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C95156FED6E00001F04D125078A0BF80', 3, 25, '生产管理', 'orderset', 90, '返工报备记录', 'returncontentdata', 1, 4, '作废', 'returncontentdata:inval', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-04-07 22:35:17', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-04-07 22:35:28', 1),"
							+ "('C9522A05CBB0000170541B801DBBA010', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 15, '导出细码列表', 'orderdata:exportitem', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-04-10 12:03:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-04-10 12:03:40', 1)");
				}

				if (version < 1.43 && newversion >= 1.43) {
					// "因收款单作废没有变更结算账户的余额 2021-04-20
					ps.addBatch("update account acc,s_company_config sc set money =round(beginmoney + ifnull((select sum(ay.money)  from accountyear ay  where ay.companyid=acc.companyid and ay.accountid=acc.accountid),0),sc.moneybit) where acc.companyid=sc.company_id");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C95573ED06800001FE48123016001D00', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 19, '详情可上传附件', 'salesorderdata:detailupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-04-20 17:16:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-04-20 17:17:08', 1)");

					// ps.addBatch("create or replace view t_order_detail_item_all_view as  select tod.id as did,tod.fstatus,tod.companyid,tod.p_finishstep,td.id as orderid,td.order_id,tod.class_id,tod.item_count,tod.max_item_count,tod.must_item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.printing,tod.billno,tod.batchno,tod.scheduletype,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,td.iproperty,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,td.originalbill,td.operate_by,td.order_remark,td.update_by ,im.outprice5,im.splits,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,td.order_time,td.finish_time,td.id,ifnull(ic.classname,'') as classname,cm.customercode,ifnull(cm.mcode,'') as cmcode,cm.customername,td.create_id,td.create_by,td.create_date,td.audit_by,td.audit_date,td.salesorderdetailid from t_order_detail tod  left join t_step_class tsc on tod.class_id=tsc.id left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid left join t_order td on tod.order_id=td.id left join customer cm on td.customer_id=cm.customerid  ");

					// 往来单位增加数据规则
					ps.addBatch("update s_permission set datarule=2 where fvalue='customerdata:read'");
					ps.addBatch("update s_permission set functionname='物料需求计算' where functionname='mrp管理'");
				}

				if (version < 1.45 && newversion >= 1.45) {
					// 20210428 修复委外单实际加工费汇总数。
					ps.addBatch("update outsourcingdetail o ,s_company_config sc set  o.actualtotal = round(ifnull((select sum(if(oi.stype='251',oi.processmoney,-oi.processmoney)) from outsourcingindetail oi where oi.relationdetailid=o.detailid and oi.`status`='1'),0),sc.moneybit) where  o.companyid=sc.company_id  and o.stype='221' and o.status<>'0'");
					ps.addBatch("update outsourcing o ,s_company_config sc set o.actualtotal = round(ifnull((select sum(oi.actualtotal) from outsourcingdetail oi where oi.outsourcingid=o.outsourcingid and oi.`status`='1' and oi.stype='221'),0),sc.moneybit) where    o.companyid=sc.company_id  and o.status<>'0'");

					ps.addBatch("ALTER TABLE `t_step` ADD COLUMN `out_price` DOUBLE NULL DEFAULT '0' COMMENT '外发单价' AFTER `stepnewid`");

					ps.addBatch("ALTER TABLE `t_progress` ADD COLUMN `out_price` DOUBLE NULL DEFAULT '0' COMMENT '外发单价' AFTER `staffcount`,ADD COLUMN `step_remark` VARCHAR(1000) NULL DEFAULT '' COMMENT '工序描述' AFTER `out_price`");
					ps.addBatch("ALTER TABLE `iteminfo` ADD COLUMN `m_finishstep` VARCHAR(50) NULL DEFAULT 'all' COMMENT '完结工序', ADD COLUMN `hasstep` INT(1) NULL DEFAULT '0' COMMENT '配置与否'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `itemstep` ( `id` varchar(36) NOT NULL COMMENT '主键', `companyid` varchar(36) DEFAULT NULL COMMENT '组织编号',"
							+ "`step_id` varchar(36) DEFAULT NULL COMMENT '工序id', `step_name` varchar(50) DEFAULT NULL COMMENT '工序名称',  `itemid` varchar(36) DEFAULT NULL COMMENT '商品id',  `class_id` varchar(36) DEFAULT NULL COMMENT '工艺id',"
							+ "`step_no` int(11) DEFAULT NULL COMMENT '工艺顺序',`step_remark` varchar(1000) DEFAULT '' COMMENT '备注',`price` double DEFAULT '0' COMMENT '单价',`out_price` double DEFAULT '0' COMMENT '外发单价', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',`create_by` varchar(36) DEFAULT NULL COMMENT '创建人',`create_date` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',`update_by` varchar(36) DEFAULT NULL COMMENT '更新人',"
							+ "`update_date` datetime DEFAULT NULL COMMENT '更新日期',PRIMARY KEY (`id`), KEY `companyid` (`companyid`),KEY `step_id` (`step_id`),KEY `itemid` (`itemid`),KEY `class_id` (`class_id`), UNIQUE KEY `itemid_step_id` (`itemid`,`step_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品工序表'");

					ps.addBatch("ALTER TABLE `t_step` CHANGE COLUMN `step_remark` `step_remark` VARCHAR(1000) NULL DEFAULT NULL COMMENT '备注' ");
				}

				if (version < 1.46 && newversion >= 1.46) {
					ps.addBatch("ALTER TABLE `prodrequisition_work_total` ADD INDEX `worksheetitemid` (`worksheetitemid`)");
					ps.addBatch("update  t_progress t set t.step_remark='' where t.step_remark='null'");

					ps.addBatch("update t_order t,s_company_config sc set t.canincount=round((select round(if(min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count)>1,1,min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count))*t.max_order_count,"
							+ " sc.countbit) as pcount from t_order_detail td left join t_progress tp on td.id = tp.detail_id and td.fstatus=1 where td.order_id=t.id "
							+ " and if(td.p_finishstep='all',1=1,td.p_finishstep=tp.step_id) group by td.order_id)-t.incount,sc.countbit) where t.companyid=sc.company_id and t.order_status=1 and (t.canincount>0 or t.canincount is null)");

					ps.addBatch("update t_order set canincount=0  where  canincount is null or canincount<0");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C9616F6A3280000188B7B8C059F51805', 3, 50, '基础模块', 'basicset', 30, '商品属性', 'itempropertydata', 1, 2, '编辑', 'itempropertydata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 22:44:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 22:45:08', 1)");

					ps.addBatch("update purchaseorder sd, s_company_config sc set sd.count = round((select sum(d.count) from purchaseorderdetail d  where sd.purchaseorderid=d.purchaseorderid),sc.countbit),sd.total = round((select sum(d.total) from purchaseorderdetail d  where sd.purchaseorderid=d.purchaseorderid),sc.moneybit) where sd.companyid=sc.company_id and sd.`status`='1' and sd.count<> round((select sum(d.count) from purchaseorderdetail d  where sd.purchaseorderid=d.purchaseorderid),sc.countbit)");

					ps.addBatch("update salesorder sd, s_company_config sc set sd.count = round((select sum(d.count) from salesorderdetail d  where sd.salesorderid=d.salesorderid),sc.countbit),sd.total = round((select sum(d.total) from salesorderdetail d  where sd.salesorderid=d.salesorderid),sc.moneybit) where sd.companyid=sc.company_id and sd.`status`='1' and sd.count<> round((select sum(d.count) from salesorderdetail d  where sd.salesorderid=d.salesorderid),sc.countbit)");

					ps.addBatch("update ordermonth om , s_company_config sc set om.purchasecount=round(ifnull((select sum(pd.count) from purchaseorderdetail pd where pd.companyid=om.companyid  and pd.`status`='1' and pd.itemid=om.itemid and pd.batchno=om.batchno and year(pd.operate_time)=om.syear and month(pd.operate_time)=om.smonth ),0),sc.countbit),om.purchasemoney=round(ifnull((select sum(pd.total) from purchaseorderdetail pd where pd.companyid=om.companyid  and pd.`status`='1' and pd.itemid=om.itemid and pd.batchno=om.batchno and year(pd.operate_time)=om.syear and month(pd.operate_time)=om.smonth ),0),sc.moneybit),om.salescount=round(ifnull((select sum(pd.count) from salesorderdetail pd where pd.companyid=om.companyid  and pd.`status`='1' and pd.itemid=om.itemid and pd.batchno=om.batchno and year(pd.operate_time)=om.syear and month(pd.operate_time)=om.smonth ),0),sc.countbit),om.salesmoney=round(ifnull((select sum(pd.total) from salesorderdetail pd where pd.companyid=om.companyid  and pd.`status`='1' and pd.itemid=om.itemid and pd.batchno=om.batchno and year(pd.operate_time)=om.syear and month(pd.operate_time)=om.smonth ),0),sc.moneybit)  where om.companyid=sc.company_id");

					ps.addBatch("update t_progress t,itemstep ip set t.step_id=ip.step_id where t.companyid=ip.companyid and t.step_id=ip.id and t.class_id=ip.class_id");
				}
				if (version < 1.47 && newversion >= 1.47) {

					ps.addBatch("CREATE TABLE IF NOT EXISTS `customerbill` (`customerbillid` varchar(36) NOT NULL COMMENT '编号',   `originalbill` varchar(50) DEFAULT NULL COMMENT '原单号', `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `orderid` varchar(36) DEFAULT NULL COMMENT '单据编号', `customerid` varchar(36) DEFAULT NULL COMMENT '往来单位', `operate_time` date DEFAULT NULL COMMENT '单据日期', `operate_by` varchar(50) DEFAULT NULL COMMENT '负责人', `smoney` double DEFAULT '0' COMMENT '调账金额', `status` varchar(1) DEFAULT NULL COMMENT '状态', `remark` varchar(200) DEFAULT '' COMMENT '备注', `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(50) DEFAULT NULL COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID', `update_by` varchar(50) DEFAULT NULL COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间', `stype` int(1) DEFAULT '1' COMMENT '类型', PRIMARY KEY (`customerbillid`), KEY `companyid` (`companyid`), KEY `customerid` (`customerid`), KEY `create_id` (`create_id`),KEY `stype` (`stype`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("ALTER TABLE `accountbill` ADD COLUMN `actcustomer` VARCHAR(150) NOT NULL DEFAULT '' COMMENT '付款单位/收款单位' AFTER `stype`");

					ps.addBatch("CREATE OR REPLACE  VIEW `accountbill_view` AS select ab.*,si.staffcode,si.staffname,c.customercode,c.customername,a.accountname from accountbill ab left join staffinfo si on ab.operate_by=si.staffid left join customer c on ab.customerid=c.customerid  left join account a on ab.accountid=a.accountid");

					ps.addBatch("ALTER TABLE `customermonth` ADD COLUMN `rec_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应收调账' AFTER `pay_outsourcing_money`,ADD COLUMN `pay_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应付调账' AFTER `rec_cmoney`");
					ps.addBatch("ALTER TABLE `customeryear` ADD COLUMN `rec_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应收调账' AFTER `pay_outsourcing_money`,ADD COLUMN `pay_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应付调账' AFTER `rec_cmoney`");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `transfer` ( `transferid` varchar(36) NOT NULL COMMENT '编号', `bill_type` varchar(2) DEFAULT NULL COMMENT '单据类型', `originalbill` varchar(50) DEFAULT NULL COMMENT '原单号', `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号', `orderid` varchar(36) DEFAULT NULL COMMENT '单据编号', `accountidout` varchar(36) DEFAULT NULL COMMENT '结算账户id', `accountidin` varchar(36) DEFAULT NULL COMMENT '结算账户id',  `operate_time` date DEFAULT NULL COMMENT '单据日期', `operate_by` varchar(50) DEFAULT NULL COMMENT '经手人', `smoney` double DEFAULT '0' COMMENT '转账金额',  `status` varchar(1) DEFAULT NULL COMMENT '状态',  `remark` varchar(200) DEFAULT '' COMMENT '转出备注',  `printing` int(11) DEFAULT '0' COMMENT '打印次数', `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID', `update_by` varchar(50) DEFAULT NULL COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`transferid`),  KEY `companyid` (`companyid`), KEY `accountidout` (`accountidout`), KEY `accountidin` (`accountidin`),  KEY `create_id` (`create_id`), KEY `bill_type` (`bill_type`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C961422957D000019C9C1BFDF640123E', 3, 35, '出纳模块', 'cashiermodel', 8, '往来单位调账', 'customerbilldata', 1, 1, '查看', 'customerbilldata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:33:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:34:57', 1), "
							+ "('C961423E32A00001E7E81BE06F881793', 3, 35, '出纳模块', 'cashiermodel', 8, '往来单位调账', 'customerbilldata', 1, 2, '新增', 'customerbilldata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:35:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:35:57', 1), "
							+ "('C961424A84B00001646612271B0013D6', 3, 35, '出纳模块', 'cashiermodel', 8, '往来单位调账', 'customerbilldata', 1, 3, '复制', 'customerbilldata:copy', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:36:14', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:38:42', 1), "
							+ "('C961426F237000012B1BE89F82D01560', 3, 35, '出纳模块', 'cashiermodel', 8, '往来单位调账', 'customerbilldata', 1, 4, '作废', 'customerbilldata:status', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:38:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:39:00', 1), "
							+ "('C9614273F680000111B1CD5311701D21', 3, 35, '出纳模块', 'cashiermodel', 8, '往来单位调账', 'customerbilldata', 1, 5, '打印', 'customerbilldata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:39:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:39:26', 1), "
							+ "('C96142798E20000178A310A19A908840', 3, 35, '出纳模块', 'cashiermodel', 8, '往来单位调账', 'customerbilldata', 1, 6, '导出', 'customerbilldata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:39:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-27 09:39:43', 1), "
							+ "('C962A456FE6000015DEC1D30874D7700', 3, 35, '出纳模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 22, '新增账户转账', 'inoutmanage:newtransfer', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:43:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:44:31', 1), "
							+ "('C962A463794000011EDE17A01CE3EF60', 3, 35, '出纳模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 23, '复制账户转账', 'inoutmanage:copytransfer', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:44:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:44:51', 1), "
							+ "('C962A468624000018ADC7CA0FD901DEA', 3, 35, '出纳模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 24, '作废账户转账', 'inoutmanage:statustransfer', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:44:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:45:20', 1), "
							+ "('C962A46F5DA00001EDE564815FAD8CD0', 3, 35, '出纳模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 25, '打印账户转账', 'inoutmanage:printtransfer', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:45:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:45:40', 1), "
							+ "('C962A474594000017A3CCE3E9DDE17B7', 3, 35, '出纳模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 26, '导出账户转账', 'inoutmanage:exporttransfer', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:45:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-05-31 16:49:25', 1), "
							+ "('C963841CF4C0000169EDA44077A01D7E', 3, 35, '出纳模块', 'cashiermodel', 7, '往来应收应付汇总', 'recpaytotal', 1, 3, '导出', 'recpaytotal:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-03 09:54:25', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2021-06-03 16:47:34', 1), "
							+ "('C9638436357000017DD6547F6D4FA9F0', 3, 35, '出纳模块', 'cashiermodel', 7, '往来应收应付汇总', 'recpaytotal', 1, 1, '查看', 'recpaytotal:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-03 09:56:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-03 09:56:08', 1), "
							+ "('C9639BC0F7D0000122F71FE0AC00122D', 3, 35, '出纳模块', 'cashiermodel', 7, '往来应收应付汇总', 'recpaytotal', 1, 2, '详情', 'recpaytotal:detail', '', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2021-06-03 16:47:34', 'cf9455f732a740aa806d7704fb68aead', '超级管理员[superman]', '2021-06-03 16:47:48', 1),"
							+ "('C965784B2A1000019BA64CD61E701AB7', 3, 25, '生产管理', 'orderset', 1, '排产单管理', 'scheduleorderdata', 1, 6, '反审', 'scheduleorderdata:reaudit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-09 11:35:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-09 11:37:33', 1),"
							+ "('C9661D1150100001B98C85D09F505000', 3, 25, '生产管理', 'orderset', 20, '派工管理', 'staffjobdata', 1, 1, '查看', 'staffjobdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-11 11:35:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-11 11:37:27', 1),"
							+ "('C9661D38A550000167757290113B1CA7', 3, 25, '生产管理', 'orderset', 20, '派工管理', 'staffjobdata', 1, 2, '派工', 'staffjobdata:job', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-11 11:38:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-11 11:38:28', 1),"
							+ "('C9661D3F807000011A8537CC1E001FCE', 3, 25, '生产管理', 'orderset', 20, '派工管理', 'staffjobdata', 1, 4, '打印', 'staffjobdata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-11 11:38:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-17 14:29:30', 1),"
							+ "('C9681569B9B00001CC37102087661CFA', 3, 25, '生产管理', 'orderset', 20, '派工管理', 'staffjobdata', 1, 3, '批量变更派工状态', 'staffjobdata:change', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-17 14:29:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-17 14:45:34', 1),"
							+ "('C969A8E1FF8000014A93CBD01E6E1661', 3, 40, '报表模块', 'reportmodel', 30, '库存状况', 'stockstatedata', 1, 1, '库存流水-导出数据', 'stockstatedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 12:00:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 12:01:04', 1),"
							+ "('C969B15A8C200001749D967010D6C660', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 20, '详情可修改信息', 'salesorderdata:modifyinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 14:28:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 14:30:19', 1),"
							+ "('C969B195585000011C35176B8C3A35A0', 3, 10, '采购模块', 'storeinmodel', 5, '采购订单管理', 'purchaseorderdata', 1, 18, '详情可修改信息', 'purchaseorderdata:modifyinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 14:32:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 14:33:14', 1),"
							+ "('C969B1B402000001897F460038641D8A', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 16, '详情可修改信息', 'outsourcingdata:modifyinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 14:34:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-06-22 14:35:13', 1)");

					ps.addBatch("update s_permission set fseq=9  where functionname='对账管理'");

					// 汇达更新不需要
					ps.addBatch("alter table t_order add premark varchar(50) not null default '' comment '打印备注'");
					ps.addBatch("alter table t_order_detail add premark varchar(50) not null default '' comment '打印备注'");
					// 汇达更新不需要

					ps.addBatch("create or replace view t_order_view as select  td.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.barcode,im.unit,im.imgurl,im.splits,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,ifnull(ic.classname,'') as classname,ifnull(cm.customercode,'') as customercode,ifnull(cm.mcode,'') as cmcode,ifnull(cm.customername,'') as customername,si.staffcode,si.staffname from t_order td left join staffinfo si on td.operate_by=si.staffid left join customer cm on td.customer_id=cm.customerid, iteminfo im left join itemclass ic on im.classid=ic.classid where td.itemid = im.itemid order by order_status asc ");

					ps.addBatch("create or replace view t_order_detail_item_view as select  tod.id,tod.fstatus,tod.companyid,tod.order_id,tod.class_id,tod.item_count,tod.item_remark,tod.itemid,tod.goods_number,tod.import_num,tod.schedulestatus,tod.scheduletype,tod.printing,tod.billno,tod.batchno,tod.must_item_count,tod.max_item_count,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.outprice,im.outprice1,im.outprice2,im.outprice3,im.outprice4,im.outprice5,im.splits,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(tsc.class_code,'') as class_code,ifnull(tsc.class_name,'') as class_name,ifnull(ic.classname,'') as classname from t_order_detail tod left join t_step_class tsc on tod.class_id=tsc.id left join iteminfo im on tod.itemid=im.itemid left join itemclass ic on im.classid=ic.classid ");
					// 汇达排产明细打印增加 end

					ps.addBatch("ALTER TABLE `t_progress` ADD COLUMN `jobdistribution` INT(1) NULL DEFAULT '0' COMMENT '派工状态' AFTER `step_remark`");

					ps.addBatch("ALTER TABLE `t_orderstep_staff` ADD COLUMN `count` DOUBLE NOT NULL DEFAULT '0' COMMENT '派工数量' AFTER `create_date`, ADD COLUMN `remark` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '派工备注' AFTER `count`,ADD COLUMN `finishcount` DOUBLE NOT NULL DEFAULT '0' COMMENT '完工数量' AFTER `remark`, ADD COLUMN `price` DOUBLE NOT NULL DEFAULT '0' COMMENT '工序单价' AFTER `finishcount`, ADD COLUMN `pricestatus` INT NULL DEFAULT '0' COMMENT '启用单价' AFTER `price`, ADD COLUMN `update_id` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '更新人id' AFTER `pricestatus`, ADD COLUMN `update_by` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '更新人' AFTER `update_id`, ADD COLUMN `update_date` DATETIME NULL DEFAULT NULL COMMENT '更新时间' AFTER `update_by`");

					ps.addBatch("update t_progress t,t_order_detail td set t.jobdistribution=3 where t.detail_id=td.id and t.jobdistribution=0 and (cast(td.schedulestatus as signed)>1  or t.progress_count>=td.item_count)");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `reqstype` INT NULL DEFAULT '2' COMMENT '物料需求计算默认下单类型' AFTER `stepbegin`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("update  purchaseorderdetail set remark='' where remark='null'");
					ps.addBatch("update  t_order set order_remark='' where order_remark='null'");
					ps.addBatch("update  outsourcingdetail set remark='' where remark='null'");

					ps.addBatch("update salesorderdetail sd set sd.hadoutsourcing=ifnull((select sum(o.count) from outsourcingdetail o where sd.detailid=o.relationdetailid and o.status<>'2' and o.stype='221'),0),sd.outsourcingcount = sd.hadoutsourcing  where (sd.outsourcingcount=0 and sd.hadoutsourcing!=0) or sd.hadoutsourcing is null");

				}

				if (version < 1.48 && newversion >= 1.48) {
					ps.addBatch("update salesorderdetail sd,outsourcingdetail od set sd.hadoutsourcing=ifnull((select sum(o.count) from outsourcingdetail o where sd.detailid=o.relationdetailid and o.status<>'2' and o.stype='221'),0),sd.outsourcingcount = sd.hadoutsourcing  where  sd.detailid=od.relationdetailid and sd.`status`='1' and (sd.outsourcingcount is null or sd.outsourcingcount=0)  and od.status<>'2' and od.stype='221'  ");

					ps.addBatch("delete from t_order_detail  where  order_id not in (select id from t_order )");

					ps.addBatch("delete from t_order_progress   where  detail_id not in (select id from t_order_detail )");

					ps.addBatch("update t_order_progress tp set tp.fstatus=2 where tp.detail_id in (select td.id from t_order_detail td where td.fstatus=2) and tp.fstatus=1");

					ps.addBatch("update purchaseorder p set p.stockstatus = '-1' where  p.stockstatus<>'3' and p.stockstatus <> '2'  and p.status='1' and p.incount>0 and (select count(pd.detailid) from purchaseorderdetail pd where pd.purchaseorderid=p.purchaseorderid"
							+ " and pd.status='1')>(select count(pd.detailid) from purchaseorderdetail pd where pd.purchaseorderid=p.purchaseorderid and pd.count<=pd.incount and pd.status='1')");

					ps.addBatch("ALTER TABLE `datachange_log` ADD INDEX `recordid` (`recordid`)");

					// 2021-07-22 1.48版本需要更新
					ps.addBatch("update outsourcingindetail oid,s_company_config sc set total = round(oid.count*oid.price,sc.moneybit) where oid.companyid=sc.company_id and  (oid.price>0 or (oid.price=0 and oid.total<>0))");

					// ps.addBatch("update purchaseorder p set p.stockstatus = 0 where p.stockstatus='-1'  and p.status='1' and p.incount=0");
				}

				if (version < 1.49 && newversion >= 1.49) {
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C9797423554000015DCB1AE9FCEA9B00', 3, 30, '库存模块', 'storemodel', 10, '调拨管理', 'storemovedata', 1, 9, '查看单价', 'storemovedata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-10 13:41:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-10 13:41:51', 1),"
							+ "('C97B6E09F590000187971C922280FC90', 3, 30, '库存模块', 'storemodel', 6, '其他出库管理', 'otheroutdata', 1, 9, '查看单价', 'otheroutdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-16 17:02:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-16 17:03:25', 1),"
							+ "('C97CCEC890E00001FB76125AA92CBD10', 3, 25, '生产管理', 'orderset', 30, '工单进度汇总', 'summaryorderdata', 1, 2, '导出数据', 'summaryorderdata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-20 23:47:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-20 23:48:06', 1)");
				}

				if (version < 1.5 && newversion >= 1.5) {
					// ps.addBatch("update purchaseorder set stockstatus=0  where stockstatus=-2");
					//
					// ps.addBatch("update t_order t set t.scheduletype=(select td.scheduletype from t_order_detail td where td.order_id=t.id order by td.create_date asc limit 1)  where t.salesorderdetailid='' and t.scheduletype=1");
					// ps.addBatch("update t_order t set t.scheduletype=2 where t.salesorderdetailid='' and t.scheduletype=1");
					// ps.addBatch("update t_order_detail td,t_order t set td.scheduletype=t.scheduletype where td.order_id=t.id and td.scheduletype!=t.scheduletype");
					//
					// ps.addBatch("update t_order t,s_company_config sc set t.canincount=round((select round(if(min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count)>1,1,min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count))*t.max_order_count,sc.countbit) as pcount from t_order_detail td left join t_progress tp on td.id = tp.detail_id and td.fstatus=1 where td.order_id=t.id "
					// +
					// " and if(td.p_finishstep='all',1=1,td.p_finishstep=tp.step_id) group by td.order_id)-t.incount ,sc.countbit) where t.companyid=sc.company_id and t.id in (select distinct pd.relationdetailid from prodstoragedetail pd where pd.companyid=t.companyid and  pd.stype='111' and pd.relationdetailid!='' and pd.status='2')");

					ps.addBatch("ALTER TABLE `itemstep` ADD COLUMN `progress_type` INT(1) NULL DEFAULT '0' COMMENT '报工类型' AFTER `out_price`");
					ps.addBatch("update itemstep it,t_step t set it.progress_type=t.progress_type where it.step_id=t.id");

					ps.addBatch("ALTER TABLE `r_mainreturn` ADD COLUMN `remark` VARCHAR(200) NULL DEFAULT '' COMMENT '备注' AFTER `update_time`");

					ps.addBatch("ALTER TABLE `t_order_progress` ADD COLUMN `return_invalid` DOUBLE NULL DEFAULT '0' COMMENT '返工报废数' AFTER `reporttype`");

					ps.addBatch("ALTER TABLE `t_progress` ADD COLUMN `progress_type` INT(1) NULL DEFAULT '0' COMMENT '报工类型' AFTER `jobdistribution`, ADD COLUMN `outcount` DOUBLE NULL DEFAULT '0' COMMENT '已外发数量' AFTER `progress_type`,ADD COLUMN `return_invalid` DOUBLE NULL DEFAULT '0' COMMENT '返工报废数' AFTER `outcount`");
					ps.addBatch("update t_progress tp,t_step t set tp.progress_type=t.progress_type where tp.step_id=t.id");
					ps.addBatch("ALTER TABLE `t_progress` ADD INDEX `progress_type` (`progress_type`)");

					// ps.addBatch("ALTER TABLE `iteminfo` ADD INDEX `classid` (`classid`)");
					// ps.addBatch("ALTER TABLE `iteminfo` DROP INDEX `mcode`, DROP INDEX `splits`");
					// ps.addBatch("ALTER TABLE `customer` ADD INDEX `typeid` (`typeid`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `stageoutsourcing` ( `stageoutsourcingid` varchar(36) NOT NULL COMMENT '编号', `bill_type` varchar(5) NOT NULL DEFAULT '' COMMENT '单据类型',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号', `orderid` varchar(36) DEFAULT '' COMMENT '单据编号', `customerid` varchar(36) DEFAULT '' COMMENT '加工单位', `operate_time` date DEFAULT NULL COMMENT '单据日期',"
							+ " `originalbill` varchar(50) DEFAULT '' COMMENT '原单号',  `operate_by` varchar(36) DEFAULT '0' COMMENT '经手人',  `stagecount` double DEFAULT '0' COMMENT '工序总数量', `count` double DEFAULT '0' COMMENT '外协总数量',  `processmoney` double DEFAULT '0' COMMENT '外协总加工费',  `stageincount` double DEFAULT '0' COMMENT '工序入总数量',  `incount` double DEFAULT '0' COMMENT '外协入总数量', `processmoneyin` double DEFAULT '0' COMMENT '外协入总加工费', `wastecount1` double DEFAULT '0' COMMENT '工序入废品总数量', `wastecount2` double DEFAULT '0' COMMENT '外协入废品总数量', `remark` varchar(200) DEFAULT '' COMMENT '备注',"
							+ "`status` varchar(1) DEFAULT '' COMMENT '单据状态', `billstatus` int(1) DEFAULT '0' COMMENT '完成状态', `printing` int(11) DEFAULT '0' COMMENT '打印次数', `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) DEFAULT '' COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建时间', `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID', `audit_by` varchar(50) DEFAULT '' COMMENT '审核人', `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID',"
							+ " `update_by` varchar(50) DEFAULT '' COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间', `iproperty` varchar(100) DEFAULT '' COMMENT '属性列表',  PRIMARY KEY (`stageoutsourcingid`),  KEY `companyid` (`companyid`),  KEY `bill_type` (`bill_type`), KEY `customerid` (`customerid`),  KEY `operate_time` (`operate_time`),  KEY `create_id` (`create_id`),  KEY `status` (`status`),  KEY `billstatus` (`billstatus`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工序外协'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `stageoutsourcingdetail` ( `detailid` varchar(36) NOT NULL COMMENT '编号', `stageoutsourcingid` varchar(36) NOT NULL COMMENT '工序外协ID', `goods_number` int(11) DEFAULT '0' COMMENT '序号', `companyid` varchar(36) DEFAULT '' COMMENT '企业编号', `orderid` varchar(36) DEFAULT '' COMMENT '单据编号',"
							+ "`customerid` varchar(36) DEFAULT '' COMMENT '加工单位', `operate_time` date DEFAULT NULL COMMENT '单据日期', `originalbill` varchar(50) DEFAULT '' COMMENT '原单号', `plandate` date DEFAULT NULL COMMENT '交货日期',  `enddate` date DEFAULT NULL COMMENT '最后交货日期', `operate_by` varchar(36) DEFAULT '0' COMMENT '经手人', `step_id` varchar(36) DEFAULT '' COMMENT '工序ID', `detail_id` varchar(36) DEFAULT '' COMMENT '工单明细ID', `order_id` varchar(36) DEFAULT '' COMMENT '工单ID', `bgoods_number` int(11) DEFAULT '0' COMMENT '工单明细序号', `progressid` varchar(36) DEFAULT '' COMMENT '进度ID',"
							+ " `dstep_remark` varchar(200) DEFAULT '' COMMENT '工序描述', `itemid` varchar(36) DEFAULT '' COMMENT '商品编号', `stype` varchar(5) DEFAULT '' COMMENT '类型', `ounit` varchar(5) DEFAULT '' COMMENT '外协单位', `dstagecount` double DEFAULT '0' COMMENT '工序数量', `dcount` double DEFAULT '0' COMMENT '外协数量', `dprice` double DEFAULT '0' COMMENT '外协单价', `dprocessmoney` double DEFAULT '0' COMMENT '外协加工费', `dstageincount` double DEFAULT '0' COMMENT '工序入数量', `dincount` double DEFAULT '0' COMMENT '外协入数量', `dprocessmoneyin` double DEFAULT '0' COMMENT '外协入加工费', `dwastecount1` double DEFAULT '0' COMMENT '工序入废品数量', `dwastecount2` double DEFAULT '0' COMMENT '外协入废品数量', `remark` varchar(200) DEFAULT '' COMMENT '备注', `status` varchar(1) DEFAULT '' COMMENT '单据状态',"
							+ " `billstatus` int(1) DEFAULT '0' COMMENT '完成状态', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) DEFAULT '' COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) DEFAULT '' COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间', `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID', `audit_by` varchar(50) DEFAULT '' COMMENT '审核人', `audit_time` datetime DEFAULT NULL COMMENT '审核时间', `relationdetailid` varchar(36) DEFAULT '' COMMENT '加工外协发货id',  `relationmainid` varchar(36) DEFAULT '' COMMENT '加工外协发货id', `relationorderid` varchar(36) DEFAULT '' COMMENT '加工外协发货单号',"
							+ " PRIMARY KEY (`detailid`), KEY `companyid` (`companyid`), KEY `stype` (`stype`), KEY `step_id` (`step_id`), KEY `itemid` (`itemid`), KEY `detail_id` (`detail_id`),  KEY `order_id` (`order_id`), KEY `progressid` (`progressid`),  KEY `customerid` (`customerid`),  KEY `operate_time` (`operate_time`),  KEY `create_id` (`create_id`),  KEY `status` (`status`),  KEY `billstatus` (`billstatus`),  KEY `stageoutsourcingid` (`stageoutsourcingid`), KEY `relationdetailid` (`relationdetailid`),  KEY `relationmainid` (`relationmainid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工序外协明细'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C97E901BFC900001141E111010B0CCA0', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 1, '查看', 'stageoutsourcingdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 10:40:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:08:28', 2),"
							+ "('C97E914DBDF00001BCE711A0A300C1E0', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 2, '新增', 'stageoutsourcingdata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:00:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:01:07', 1),"
							+ "('C97E91516190000147D319C4BF709A60', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 3, '修改', 'stageoutsourcingdata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:01:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:07:23', 1),"
							+ "('C97E916958C0000136BFDD501C8019F2', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 4, '详情', 'stageoutsourcingdata:detail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:02:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:07:25', 1),"
							+ "('C97E917225F000014E752B30180B1124', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 5, '删除', 'stageoutsourcingdata:del', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:03:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:07:27', 1),"
							+ "('C97E91768EA00001201C799B1CF01478', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 6, '审核', 'stageoutsourcingdata:audit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:03:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:07:30', 1),"
							+ "('C97E9191C3B00001FC111254F88016C4', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 8, '修改完成状态', 'stageoutsourcingdata:billstatus', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:05:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:41:02', 1),"
							+ "('C97E919B49300001C66CEEE01F998820', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 9, '导出汇总', 'stageoutsourcingdata:exporttotal', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:06:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:41:06', 1),"
							+ "('C97E91A240E00001C7CE139C169087A0', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 10, '导出明细', 'stageoutsourcingdata:exportdetail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:06:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:41:10', 1),"
							+ "('C97E91A78DE0000124D0A53096F02A20', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 11, '打印', 'stageoutsourcingdata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:07:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:41:13', 1),"
							+ "('C97E91B34650000188A9AA50158F4AB0', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 1, '查看', 'stageoutsourcingindata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:07:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:08:41', 2),"
							+ "('C97E91C10F7000015A22BB2112F612BB', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 2, '新增', 'stageoutsourcingindata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:08:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:09:38', 1),"
							+ "('C97E91C60E5000013280A2D01BD944D0', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 3, '修改', 'stageoutsourcingindata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:09:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:09:36', 1),"
							+ "('C97E91CF16E0000173BF190015E01440', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 4, '详情', 'stageoutsourcingindata:detail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:09:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:10:06', 1),"
							+ "('C97E91D71C0000013DA0190016BF6770', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 5, '删除', 'stageoutsourcingindata:del', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:10:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:10:32', 1),"
							+ "('C97E91DBEA600001103014E010F47810', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 6, '审核', 'stageoutsourcingindata:audit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:10:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:10:51', 1),"
							+ "('C97E91E4EA500001D6BD1460FD121D4D', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 9, '导出汇总', 'stageoutsourcingindata:exporttotal', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:11:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-26 14:08:22', 1),"
							+ "('C97E91EE524000015B17173841AC1D06', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 10, '导出明细', 'stageoutsourcingindata:exportdetail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:11:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-26 14:08:25', 1),"
							+ "('C97E91F248600001E35E1280125719A8', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 11, '打印', 'stageoutsourcingindata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:12:07', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-03 20:04:03', 1),"
							+ "('C97E920CC9400001B89040101470107A', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 12, '查看单价', 'stageoutsourcingindata:showprice', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:13:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-03 20:04:05', 1),"
							+ "('C97E9213CA800001884319C0107C1693', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 12, '查看单价', 'stageoutsourcingdata:showprice', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-08-26 11:14:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:41:18', 1),"
							+ "('C982EF5E59C00001BE4299F016301BF0', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 7, '反审', 'stageoutsourcingdata:reaudit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:40:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:40:57', 1),"
							+ "('C982EF6AF4800001F6B01F60836618E3', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 8, '删除作废', 'stageoutsourcingindata:delstatus', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-09 00:41:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-03 18:05:23', 1),"
							+ "('C988965350E000016EB71428186ADCB0', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 7, '作废', 'stageoutsourcingindata:status', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-09-26 14:07:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-03 18:05:25', 1),"
							+ "('C98B3E9E87A0000143CAF02928E08980', 3, 25, '生产管理', 'orderset', 13, '工序外协商品查询', 'orderstageoutdata', 1, 1, '查看', 'orderstageoutdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-04 20:16:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-04 20:18:02', 1),"
							+ "('C98C364679D00001CB497AB015701D3C', 3, 25, '生产管理', 'orderset', 13, '工序外协商品查询', 'orderstageoutdata', 1, 2, '导出数据', 'orderstageoutdata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-07 20:25:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2021-10-07 20:25:17', 1)");

					ps.addBatch("update  prodrequisition_work_total pw,t_order t,s_company_config s set pw.needcount=round(t.order_count*pw.unitcount,s.countbit)  where pw.worksheetid=t.id and t.companyid=s.company_id and pw.unitcount>0   and pw.needcount<>pw.count and  (pw.needcount-round(concat(pw.unitcount*t.order_count,''),s.countbit)>1 or pw.needcount-round(concat(pw.unitcount*t.order_count,''),s.countbit)<-1 ) ");
				}

				if (version < 1.6 && newversion >= 1.6) {// 1.6修复往来应收应付调帐作废没有扣减往来单位的应收应付余额的问题。
					ps.addBatch("update t_order t,salesorderdetail sd set t.plandate=sd.plandate where t.salesorderdetailid=sd.detailid");

					ps.addBatch("update t_progress tp,t_step t set tp.progress_type=t.progress_type where tp.step_id=t.id and tp.progress_type is null");
				}

				if (version < 1.61 && newversion >= 1.61) {
					ps.addBatch("ALTER TABLE `t_order_progress` ADD COLUMN `begintime` DATETIME NULL DEFAULT NULL COMMENT '开始时间' AFTER `return_invalid`,ADD COLUMN `mincount` INT(11) NULL DEFAULT '0' COMMENT '分钟数' AFTER `begintime`,ADD COLUMN `recordtime` DATETIME NULL DEFAULT NULL COMMENT '记录时间' AFTER `mincount`");
					ps.addBatch("update t_order_progress set recordtime=create_date where 1=1");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `everystep_begin` INT(1) NULL DEFAULT '0' COMMENT '每次工序报工必需填写开始时间' AFTER `reqstype`,ADD COLUMN `reportlimit` INT(1) NULL DEFAULT '0' COMMENT '线性工艺的后端报工受前一工序完成数量限制' AFTER `everystep_begin`");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");
					ps.addBatch("update t_progress tp,t_step t set tp.progress_type=t.progress_type where tp.step_id=t.id and tp.progress_type is null");

					ps.addBatch("update scheduleorder set schedulestatus=if((select count(id) from t_order where scheduleid=scheduleorder.scheduleid and schedulestatus='3')>0,'3','2') where scheduleid=scheduleorder.scheduleid and schedulestatus='1'  and (select count(id) from  t_order where scheduleid=scheduleorder.scheduleid)=(select count(id) from t_order where scheduleid=scheduleorder.scheduleid and schedulestatus>1)");
				}

				if (version < 1.62 && newversion >= 1.62) {
					ps.addBatch("update salesorderdetail s set s.schedulcount=s.count where s.schedulcount>s.count and s.scheduledcount=0 and s.`status`>0");
					ps.addBatch("update salesorderdetail s,s_company_config sc set s.scheduledcount = round(ifnull((select sum(order_count) from t_order where salesorderdetailid=s.detailid and order_status<>2),0),sc.countbit),s.schedulcount=if(s.scheduledcount>=s.count,0,round(s.count-s.scheduledcount,sc.countbit))  where s.companyid=sc.company_id and s.schedulcount>s.count and s.`status`>0");

					ps.addBatch("update t_order_detail td,t_order t,s_company_config sc set td.must_item_count=round(td.import_num*t.max_order_count,sc.countbit) where td.order_id=t.id and td.companyid=sc.company_id and td.max_item_count<>td.must_item_count and td.must_item_count<>round(td.import_num*t.max_order_count,sc.countbit)");
					ps.addBatch("update t_order t,s_company_config sc set t.canincount=round((select round(if(min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count)>1,1,min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count))*t.max_order_count,"
							+ " sc.countbit) as pcount from t_order_detail td left join t_progress tp on td.id = tp.detail_id and td.fstatus=1 where td.order_id=t.id "
							+ " and if(td.p_finishstep='all',1=1,td.p_finishstep=tp.step_id) group by td.order_id)-t.incount,sc.countbit) where t.companyid=sc.company_id and t.order_status=1 and t.max_order_count>t.order_count");

				}

				if (version < 1.63 && newversion >= 1.63) {
					ps.addBatch("update customermonth cm,s_company_config sc set cm.rec_cmoney=round(ifnull((select sum(if(stype=1 or stype=2,cb.smoney,0)) from customerbill cb where cb.customerid=cm.customerid and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.pay_cmoney=round(ifnull((select sum(if(stype=1 or stype=3,cb.smoney,0)) from customerbill cb where cb.customerid=cm.customerid and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit),cm.receivable=round(cm.rec_sellout_money-cm.rec_sellin_money-rec_money-rec_cmoney,sc.moneybit),cm.payable=round(cm.pay_purchasein_money-cm.pay_purchaseout_money+cm.pay_outsourcing_money-cm.pay_money-cm.pay_cmoney,sc.moneybit) where cm.companyid=sc.company_id");
					ps.addBatch("update customeryear cm,s_company_config sc set cm.rec_cmoney=round(ifnull((select sum(if(stype=1 or stype=2,cb.smoney,0)) from customerbill cb where cb.customerid=cm.customerid and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit),cm.pay_cmoney=round(ifnull((select sum(if(stype=1 or stype=3,cb.smoney,0)) from customerbill cb where cb.customerid=cm.customerid and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit),cm.receivable=round(cm.rec_sellout_money-cm.rec_sellin_money-rec_money-rec_cmoney,sc.moneybit),cm.payable=round(cm.pay_purchasein_money-cm.pay_purchaseout_money+cm.pay_outsourcing_money-cm.pay_money-cm.pay_cmoney,sc.moneybit) where cm.companyid=sc.company_id");
					ps.addBatch("update customer cm,s_company_config sc set cm.receivable=round(ifnull((select sum(cb.receivable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.beginreceivable,sc.moneybit),cm.payable=round(ifnull((select sum(cb.payable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.beginpayable,sc.moneybit)  where cm.companyid=sc.company_id");
				}

				if (version < 1.64 && newversion >= 1.64) {
					ps.addBatch("update outsourcing o ,outsourcingdetail od set o.customerid=od.customerid  where o.outsourcingid=od.outsourcingid and  o.customerid<>od.customerid");
					ps.addBatch("ALTER TABLE `salesorderdetail` DROP INDEX `salesorderid`,ADD INDEX `salesorderid` (`salesorderid`, `status`)");
					ps.addBatch("ALTER TABLE `t_progress` ADD INDEX `jobdistribution` (`jobdistribution`)");
					ps.addBatch("ALTER TABLE `s_roles` CHANGE COLUMN `description` `description` VARCHAR(100) NULL DEFAULT NULL COMMENT '描述' AFTER `stype`");

				}

				if (version < 1.65 && newversion >= 1.65) {

					ps.addBatch("update customermonth cm,s_company_config sc set cm.rec_sellout_money=round(ifnull((select sum(cb.total) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='2' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.rec_sellin_money=round(ifnull((select sum(cb.total) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='7' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit),cm.rec_add_money=round(cm.rec_sellout_money-cm.rec_sellin_money,sc.moneybit),cm.receivable=round(cm.rec_sellout_money-cm.rec_sellin_money-rec_money-rec_cmoney,sc.moneybit)  where cm.companyid=sc.company_id");
					ps.addBatch("update customeryear cm,s_company_config sc set cm.rec_sellout_money=round(ifnull((select sum(cb.total) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='2' and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.rec_sellin_money=round(ifnull((select sum(cb.total) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='7' and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit),cm.rec_add_money=round(cm.rec_sellout_money-cm.rec_sellin_money,sc.moneybit),cm.receivable=round(cm.rec_sellout_money-cm.rec_sellin_money-rec_money-rec_cmoney,sc.moneybit) where cm.companyid=sc.company_id");
					ps.addBatch("update customer cm,s_company_config sc set cm.receivable=round(ifnull((select sum(cb.receivable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.beginreceivable,sc.moneybit)  where cm.companyid=sc.company_id");

					ps.addBatch("update stageoutsourcingdetail sd set sd.progressid=ifnull((select p.id from t_progress p where p.detail_id=sd.detail_id and p.step_id=sd.step_id),'') where sd.status='1'");
					ps.addBatch("update t_progress p,s_company_config sc set p.outcount=round(ifnull((select sum(sd.dstagecount) from stageoutsourcingdetail sd where sd.progressid=p.id and sd.stype='311' and sd.`status`='1'),0),sc.countbit),p.progress_count=round(ifnull((select sum(sd.dstageincount) from stageoutsourcingdetail sd where sd.progressid=p.id and sd.stype='311' and sd.`status`='1'),0),sc.countbit),p.invalid_count=round(ifnull((select sum(sd.dwastecount1) from stageoutsourcingdetail sd where sd.progressid=p.id and sd.stype='311' and sd.`status`='1'),0),sc.countbit) where p.companyid=sc.company_id and p.progress_type=2");

					ps.addBatch("update t_order t  set t.canincount=round(ifnull((select round(if(min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count)>1,1,min(ifnull(tp.progress_count-(tp.freturn_count-tp.return_count),0)/td.must_item_count))*t.max_order_count,3) as pcount from t_order_detail td left join t_progress tp on td.id = tp.detail_id and td.fstatus=1 where td.order_id=t.id group by td.order_id)-t.incount,0),3) where t.order_status=1 and ifnull((select count(*) from t_progress p where p.order_id=t.id and p.progress_type=2 limit 1),0)>0");

					ps.addBatch("ALTER TABLE `prodstoragedetail` ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `salesorderorderid` (`salesorderorderid`),ADD INDEX `salesorderdetailid` (`salesorderdetailid`)");
					ps.addBatch("ALTER TABLE `storeindetail` ADD INDEX `relationmainid` (`relationmainid`), ADD INDEX `relationdetailid` (`relationdetailid`)");
					ps.addBatch("ALTER TABLE `deliverdetail` ADD INDEX `relationmainid` (`relationmainid`)");
					ps.addBatch("ALTER TABLE `outsourcingdetail` ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`)");
					ps.addBatch("ALTER TABLE `outsourcingindetail` ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`), ADD INDEX `salesorderdetailid` (`salesorderdetailid`), ADD INDEX `salesorderorderid` (`salesorderorderid`), ADD INDEX `customerid` (`customerid`), ADD INDEX `returndetailid` (`returndetailid`)");
					ps.addBatch("ALTER TABLE `processinoutdetail` ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`)");
					ps.addBatch("ALTER TABLE `prodrequisitiondetail`  ADD INDEX `relationtotalid` (`relationtotalid`)");
					ps.addBatch("ALTER TABLE `purchaseorderdetail` ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`)");
					ps.addBatch("ALTER TABLE `storeindetail` ADD INDEX `returndetailid` (`returndetailid`)");
					ps.addBatch("ALTER TABLE `storeoutdetail` ADD INDEX `returndetailid` (`returndetailid`),ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`)");
					ps.addBatch("ALTER TABLE `t_detail_progress` ADD INDEX `order_progressid` (`order_progressid`)");
					ps.addBatch("ALTER TABLE `t_order_detail` ADD INDEX `p_finishstep` (`p_finishstep`),ADD INDEX `scheduletype` (`scheduletype`), ADD INDEX `schedulestatus` (`schedulestatus`)");
					ps.addBatch("ALTER TABLE `iteminfo` ADD INDEX `m_finishstep` (`m_finishstep`)");
					ps.addBatch("ALTER TABLE `s_company` ADD INDEX `companyid` (`companyid`), ADD INDEX `s_parentid` (`s_parentid`), ADD INDEX `fstatus` (`fstatus`)");
					ps.addBatch("ALTER TABLE `s_company_config` ADD INDEX `lifelimit` (`lifelimit`), ADD INDEX `enddate` (`enddate`)");
					ps.addBatch("ALTER TABLE `s_logincheck` ADD INDEX `userid` (`userid`),ADD INDEX `appcode` (`appcode`)");
					ps.addBatch("ALTER TABLE `wechataccount` ADD INDEX `companyid` (`companyid`)");
					ps.addBatch("ALTER TABLE `s_userinfo` ADD INDEX `username` (`username`), ADD INDEX `phone` (`phone`),ADD INDEX `role` (`role`), ADD INDEX `roletype` (`roletype`)");
					ps.addBatch("ALTER TABLE `staffinfo` ADD INDEX `userid` (`userid`)");

				}

				if (version < 1.66 && newversion >= 1.66) {
					ps.addBatch("ALTER TABLE `t_step` ADD INDEX `progress_type` (`progress_type`)");

					ps.addBatch("delete from itemstep where (select 1 from iteminfo im where  im.itemid=itemstep.itemid and im.class_id<>itemstep.class_id limit 1)");

					ps.addBatch("update itemstep ip,t_step t set ip.progress_type=t.progress_type where  ip.step_id=t.id  and ip.progress_type<>t.progress_type");
					// ps.addBatch("update itemstep set progress_type=2 where  (select 1 from t_step where id=itemstep.step_id and progress_type=2 and progress_type<>itemstep.progress_type)");
					ps.addBatch("update t_progress set progress_type=2  where (select 1 from t_step where id=t_progress.step_id and progress_type=2 and progress_type<>t_progress.progress_type)");

					ps.addBatch("update scheduleorder s,s_company_config sc set s.count=round((select sum(order_count) from t_order where scheduleid=s.scheduleid),sc.countbit) where  s.companyid=sc.company_id and s.count<>round((select sum(order_count) from t_order where scheduleid=s.scheduleid),sc.countbit)");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C9CCC5517F00000132C11D7911C0134C', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 21, '出货', 'salesorderdata:out', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-26 10:16:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-26 10:16:28', 1),"
							+ "('C9CCD590E28000013CAD78801A1016EB', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 17, '收货', 'outsourcingdata:canin', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-26 14:59:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-26 15:00:24', 1),"
							+ "('C9CCD597A700000152591C80FC701C42', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 18, '出料', 'outsourcingdata:canout', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-26 15:00:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-26 15:00:43', 1),"
							+ "('C9CD2509F4A00001C6716CF01E40DB00', 3, 25, '生产管理', 'orderset', 1, '排产单管理', 'scheduleorderdata', 1, 16, '入库', 'scheduleorderdata:canin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-27 14:08:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-27 14:10:12', 1),"
							+ "('C9CD28FBB1D00001D624EDB317408D00', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 16, '领料', 'orderdata:canout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-27 15:17:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-27 15:18:02', 1),"
							+ "('C9CD7D6CA3700001FE141F702BEF7130', 4, 35, '财务模块', 'cashiermodel', 30, '总账管理', 'financemanage', 1, 1, '查看', 'financemanage:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-28 15:53:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-28 15:54:17', 1),"
							+ "('C9CD7D9271600001EF4B1B6267703F80', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 13, '收货', 'stageoutsourcingdata:canin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-28 15:56:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-04-28 15:57:42', 1)");

					ps.addBatch("update s_permission set parentname='' where parentname='财务模块'");
				}

				if (version < 1.67 && newversion >= 1.67) {
					ps.addBatch("update s_permission set parentname='财务模块' where parentname='出纳模块'");
					ps.addBatch("update s_permission set parentname='财务模块' where id='C9CD7D6CA3700001FE141F702BEF7130'");
				}

				if (version < 1.68 && newversion >= 1.68) {
					// ps.addBatch("ALTER TABLE `t_stepnew` CHANGE COLUMN `stepnewname` `stepnewname` VARCHAR(100) NULL DEFAULT NULL COMMENT '工序名称' AFTER `stepnewcode`");
					// ps.addBatch("ALTER TABLE `t_step` CHANGE COLUMN `step_name` `step_name` VARCHAR(100) NULL DEFAULT NULL COMMENT '工序名称' AFTER `step_code`");
					ps.addBatch("update s_permission set pseq=20  where id='C9CCC5517F00000132C11D7911C0134C'");
					ps.addBatch("CREATE OR REPLACE  VIEW `s_companyconfig` AS select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate,0 as activecount  from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `s_login_log` ADD COLUMN `checktime` DATETIME NULL DEFAULT NULL COMMENT '结束登录时间' AFTER `operatetime`");
					ps.addBatch("update s_login_log s set  s.checktime=s.operatetime where 1=1");
					ps.addBatch("ALTER TABLE `s_login_log` ADD INDEX `logintime` (`logintime`)");

					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `checkLG` INT(1) NOT NULL DEFAULT '1' COMMENT '检查'  AFTER `prefessions`");
				}

				if (version < 2.0 && newversion >= 2.0) {
					// 云端erp已删除 南通奥斯特不要，其他要
					ps.addBatch("ALTER TABLE `t_order_detail` DROP INDEX `companyid_id`, DROP INDEX `companyid_billno`, ADD INDEX `companyid_billno` (`billno`)");

					ps.addBatch("update  t_progress t set t.finish_time=t.update_date where t.progress_count=t.outcount and t.outcount>0 and t.finish_time is null");

					ps.addBatch("delete from houselimit where  uplimit=0 and  lowlimit=0");
					ps.addBatch("update salesorderdetail sd,s_company_config sc set schedulcount=if(count>scheduledcount,round(count-scheduledcount,sc.countbit ),0) where sd.companyid=sc.company_id and sd.status=1 and (sd.schedulcount<0 or (sd.count>sd.scheduledcount and sd.count<>round(sd.scheduledcount+sd.schedulcount,sc.countbit)))");

					ps.addBatch("ALTER TABLE `t_stepnew` CHANGE COLUMN `stepnewid` `stepnewid` VARCHAR(36) NOT NULL COMMENT '主键' FIRST, ADD PRIMARY KEY (`stepnewid`)");
					ps.addBatch("ALTER TABLE `t_stepnew` ADD COLUMN `step_workshop` VARCHAR(36) NULL DEFAULT '' COMMENT '车间编号' AFTER `stepnewname`,   ADD COLUMN `s_work_mode` INT(1) NULL DEFAULT '1' COMMENT '计工方式' AFTER `step_workshop`, CHANGE COLUMN `remark` `remark` VARCHAR(1000) NULL DEFAULT '' COMMENT '备注' AFTER `s_work_mode`, ADD INDEX `step_workshop` (`step_workshop`), ADD INDEX `s_work_mode` (`s_work_mode`)");
					ps.addBatch("ALTER TABLE `t_step` ADD COLUMN `progress_scale` Double NULL DEFAULT '1' COMMENT '报工比例' AFTER `progress_type`");

					ps.addBatch("ALTER TABLE `itemstep` ADD COLUMN `stepnewid` VARCHAR(36) NULL DEFAULT NULL AFTER `class_id`, ADD COLUMN `step_workshop` VARCHAR(36) NULL DEFAULT '' AFTER `stepnewid`, ADD COLUMN `progress_scale` Double NULL DEFAULT '1' COMMENT '报工比例' AFTER `stepnewid`, ADD INDEX `stepnewid` (`stepnewid`)");
					ps.addBatch("update itemstep ip,t_step t set ip.stepnewid = t.stepnewid where ip.step_id = t.id");
					ps.addBatch("ALTER TABLE `t_stepnew_role` CHANGE COLUMN `id` `id` VARCHAR(36) NOT NULL COMMENT '主键' FIRST, ADD PRIMARY KEY (`id`)");
					ps.addBatch("ALTER TABLE `t_stepnew_userid` CHANGE COLUMN `id` `id` VARCHAR(36) NOT NULL COMMENT '主键' FIRST, ADD PRIMARY KEY (`id`)");
					ps.addBatch("ALTER TABLE `t_order_progress` CHANGE COLUMN `companyid` `companyid` VARCHAR(36) NULL DEFAULT NULL COMMENT '组织编号' AFTER `fstatus`, CHANGE COLUMN `order_id` `order_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '单据编号' AFTER `companyid`, CHANGE COLUMN `detail_id` `detail_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '物料编号' AFTER `order_id`, CHANGE COLUMN `step_id` `step_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '工艺编号' AFTER `detail_id`, CHANGE COLUMN `device_id` `device_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '设备编号' AFTER `step_id`, CHANGE COLUMN `workshop_id` `workshop_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '车间编号' AFTER `device_id`,"
							+ " CHANGE COLUMN `user_id` `user_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '生产员工' AFTER `workshop_id`, ADD COLUMN `progress_id` VARCHAR(36) NULL DEFAULT '' COMMENT 'progress_id' AFTER `user_id`, ADD COLUMN `stepnewid` VARCHAR(36) NULL DEFAULT '' COMMENT 'stepnewid' AFTER `progress_id`, ADD INDEX `progress_id` (`progress_id`), ADD INDEX `stepnewid` (`stepnewid`), ADD INDEX `fstatus` (`fstatus`),ADD COLUMN `invalidreason` VARCHAR(100) NULL DEFAULT '' COMMENT '作废原因' AFTER `recordtime`");

					ps.addBatch("ALTER TABLE `t_progress` CHANGE COLUMN `companyid` `companyid` VARCHAR(36) NULL DEFAULT NULL COMMENT '组织编号' AFTER `fstatus`,"
							+ " CHANGE COLUMN `order_id` `order_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '单据编号' AFTER `companyid`, CHANGE COLUMN `detail_id` `detail_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '明细编号' AFTER `order_id`, CHANGE COLUMN `class_id` `class_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '工艺编号' AFTER `detail_id`, CHANGE COLUMN `step_id` `step_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '流程编号' AFTER `class_id`,"
							+ " ADD COLUMN `stepnewid` VARCHAR(36) NULL DEFAULT '' COMMENT 'stepnewid' AFTER `step_id`,ADD COLUMN `step_name` VARCHAR(50) NULL DEFAULT '' COMMENT '工序名称' AFTER `stepnewid`, ADD COLUMN `progress_scale` Double NULL DEFAULT '1' COMMENT '报工比例' AFTER `progress_type`, ADD COLUMN `step_no` INT(11) NULL DEFAULT '1' COMMENT '序号' AFTER `progress_scale`, ADD INDEX `stepnewid` (`stepnewid`),  ADD COLUMN `work_count` DOUBLE NULL DEFAULT '0' COMMENT '生产数量' AFTER `return_invalid`, ADD COLUMN `max_work_count` DOUBLE NULL DEFAULT '0' COMMENT '最大生产数量' AFTER `work_count`,ADD COLUMN `step_workshop` VARCHAR(36) NULL DEFAULT '' COMMENT '生产车间' AFTER `max_work_count`,ADD INDEX `step_workshop` (`step_workshop`),ADD COLUMN `must_work_count` DOUBLE NULL DEFAULT '0' COMMENT '必需生产数量' AFTER `max_work_count`,	ADD COLUMN `prefinishdate` DATE NULL DEFAULT NULL COMMENT '预计完成日期' AFTER `step_workshop`,ADD INDEX `prefinishdate` (`prefinishdate`),"
							+ " ADD COLUMN `incount` DOUBLE NULL DEFAULT '0' COMMENT '已收货数量' AFTER `must_work_count`, ADD COLUMN `in_invalid` DOUBLE NULL DEFAULT '0' COMMENT '已收货废品' AFTER `incount`, ADD COLUMN `in_return` DOUBLE NULL DEFAULT '0' COMMENT '返工收货' AFTER `in_invalid`, ADD COLUMN `in_return_invalid` DOUBLE NULL DEFAULT '0' COMMENT '返工收货废品' AFTER `in_return`, ADD COLUMN `fout_return` DOUBLE NULL DEFAULT '0' COMMENT '返工发货' AFTER `in_return_invalid`,	ADD COLUMN `class_type` INT NULL DEFAULT '1' COMMENT '工艺类型' AFTER `prefinishdate`, ADD COLUMN `finishcount` DOUBLE NULL DEFAULT '0' COMMENT '完成数量' AFTER `class_type`,ADD COLUMN `isfinish` INT NULL DEFAULT '0' COMMENT '是否完成' AFTER `finishcount`");

					ps.addBatch("update t_progress t,t_order_detail td set t.work_count=td.item_count,t.must_work_count=td.must_item_count,t.max_work_count=td.max_item_count,t.class_type=if(t.class_id='101',1,if(t.class_id='102',2,(select tc.class_type from t_step_class tc where tc.id=t.class_id))) where t.detail_id=td.id");

					ps.addBatch("update t_progress ip,t_step t set ip.out_price=if(ip.out_price is null,0,ip.out_price),ip.stepnewid = t.stepnewid,ip.step_name=t.step_name,ip.step_no=if(length(t.class_id)=3,ip.step_no,t.step_no) where ip.step_id = t.id");

					ps.addBatch("update t_progress set incount = progress_count,in_invalid=invalid_count where progress_type=2");

					ps.addBatch("ALTER TABLE `t_order_progress` ADD COLUMN `audit_id` VARCHAR(36) NULL DEFAULT '' COMMENT '审核人ID' AFTER `update_date`, ADD COLUMN `audit_by` VARCHAR(36) NULL DEFAULT '' COMMENT '审核人' AFTER `audit_id`, ADD COLUMN `audit_date` DATETIME NULL DEFAULT NULL COMMENT '审核日期' AFTER `audit_by`, ADD COLUMN `step_no` INT NULL DEFAULT '1' COMMENT '工序序号' AFTER `invalidreason`,ADD INDEX `audit_date` (`audit_date`)");

					ps.addBatch("update t_order_progress tp,t_progress t  set tp.progress_id=t.id, tp.stepnewid = t.stepnewid,tp.step_no=t.step_no where t.detail_id=tp.detail_id and t.step_id=tp.step_id");

					ps.addBatch("update t_order_progress tp,t_step t  set tp.stepnewid = t.stepnewid,tp.step_no=t.step_no where  tp.step_id=t.id and tp.stepnewid='' ");

					ps.addBatch("ALTER TABLE `t_detail_progress` CHANGE COLUMN `companyid` `companyid` VARCHAR(36) NULL DEFAULT NULL COMMENT '组织编号' AFTER `fstatus`, CHANGE COLUMN `order_id` `order_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '单据编号' AFTER `companyid`, CHANGE COLUMN `detail_id` `detail_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '单据明细编号' AFTER `order_id`, CHANGE COLUMN `step_id` `step_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '工艺编号' AFTER `detail_no`, ADD COLUMN `stepnewid` VARCHAR(36) NULL DEFAULT '' COMMENT 'stepnewid' AFTER `step_id`, ADD INDEX `stepnewid` (`stepnewid`)");

					ps.addBatch("ALTER TABLE `t_detail_code` CHANGE COLUMN `companyid` `companyid` VARCHAR(36) NULL DEFAULT NULL COMMENT '组织编号' AFTER `fstatus`, CHANGE COLUMN `order_id` `order_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '单据编号' AFTER `companyid`, CHANGE COLUMN `detail_id` `detail_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '单据明细编号' AFTER `order_id`, CHANGE COLUMN `step_id` `step_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '工艺编号' AFTER `detail_no`");

					ps.addBatch("update t_detail_progress ip,t_step t set ip.stepnewid = t.stepnewid where ip.step_id = t.id");

					ps.addBatch("ALTER TABLE `t_detail_progress` ADD COLUMN `progress_id` VARCHAR(36) NULL DEFAULT '' COMMENT 'progress_id' AFTER `stepnewid`, ADD INDEX `progress_id` (`progress_id`)");

					ps.addBatch("update t_detail_progress tp,t_progress t  set tp.progress_id=t.id where t.detail_id=tp.detail_id and t.step_id=tp.step_id");

					ps.addBatch("ALTER TABLE `scheduleorder` ADD COLUMN `customerid` VARCHAR(36) NULL DEFAULT '' COMMENT '生产部门' AFTER `companyid`, ADD COLUMN `project` VARCHAR(50) NULL DEFAULT '' COMMENT '生产项目' AFTER `customerid`, ADD INDEX `customerid` (`customerid`), ADD INDEX `project` (`project`)");
					ps.addBatch("ALTER TABLE `t_order` ADD COLUMN `customerid` VARCHAR(36) NULL DEFAULT '' COMMENT '生产部门' AFTER `companyid`, ADD COLUMN `project` VARCHAR(50) NULL DEFAULT '' COMMENT '生产项目' AFTER `customerid`, ADD INDEX `customerid` (`customerid`), ADD INDEX `project` (`project`),ADD COLUMN `isurgent` INT(1) NOT NULL DEFAULT '0' COMMENT '是否加急' AFTER `premark`,ADD INDEX `isurgent` (`isurgent`),ADD COLUMN `finishcount` DOUBLE NOT NULL DEFAULT '0' COMMENT '完成数量' AFTER `isurgent`");

					ps.addBatch("ALTER TABLE `t_order_detail`  ADD INDEX `class_id` (`class_id`), ADD COLUMN `t_finishcount` DOUBLE NOT NULL DEFAULT '0' COMMENT '完成数量' AFTER `premark`,ADD COLUMN `t_invailcount` DOUBLE NOT NULL DEFAULT '0' COMMENT '报废数量' AFTER `t_finishcount`");

					ps.addBatch("update itemstep it,t_step ts set it.step_no=ts.step_no where it.class_id=ts.class_id and it.step_id=ts.id");
					ps.addBatch("ALTER TABLE `itemstep` DROP INDEX `itemid_step_id`");

					// update t_order_detail td,s_company_config sc set
					// td.t_finishcount=round(ifnull((select
					// min((tp.progress_count-(tp.freturn_count-tp.return_count))/tp.progress_scale)
					// from t_progress tp where tp.detail_id=td.id and
					// if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0),sc.countbit),td.t_invailcount=round(ifnull((select
					// sum(tp.invalid_count/tp.progress_scale) from t_progress
					// tp where tp.detail_id=td.id and
					// if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0),sc.countbit)
					// where td.companyid=sc.company_id;
					// update t_order tr,s_company_config sc set
					// tr.finishcount=round(ifnull((select
					// min(if(td.t_finishcount/td.must_item_count>1,1,(td.t_finishcount/td.must_item_count)))
					// from t_order_detail td where
					// td.order_id=tr.id),0)*tr.max_order_count,sc.countbit),tr.canincount=if(tr.finishcount>tr.incount,round(tr.finishcount-tr.incount,sc.countbit),0)
					// where tr.companyid=sc.company_id;

					ps.addBatch("update iteminfo i set i.m_finishstep=ifnull((select ip.id from itemstep ip  where ip.id=i.m_finishstep or ip.itemid=i.itemid and ip.step_id=i.m_finishstep limit 1),'all')  where i.m_finishstep!='all' ");
					ps.addBatch("update t_order_detail i set i.p_finishstep=ifnull((select ip.id from t_progress ip  where ip.detail_id=i.id and (ip.id=i.p_finishstep or ip.step_id=i.p_finishstep) limit 1),'all')   where i.p_finishstep!='all' ");

					ps.addBatch("update t_order_detail td,s_company_config sc set td.t_finishcount=round(ifnull((select min((tp.progress_count-(tp.freturn_count-tp.return_count))/tp.progress_scale) from t_progress tp where tp.detail_id=td.id and if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0),sc.countbit),td.t_invailcount=round(ifnull((select sum(tp.invalid_count/tp.progress_scale) from t_progress tp where tp.detail_id=td.id and if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0),sc.countbit) where td.companyid=sc.company_id");
					ps.addBatch("update t_order tr,s_company_config sc set tr.finishcount=round(ifnull((select min(if(td.t_finishcount/td.must_item_count>1,1,(td.t_finishcount/td.must_item_count))) from t_order_detail td where td.order_id=tr.id),0)*tr.max_order_count,sc.countbit) where tr.companyid=sc.company_id");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `project` ( `projectid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `seq` int(11) DEFAULT '0' COMMENT '排序号',  `project` varchar(50) DEFAULT '' COMMENT '生产项目', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID', `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`projectid`),  KEY `companyid` (`companyid`),  KEY `project` (`project`),  KEY `create_id` (`create_id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C9E0C886E0400001617D4993D3061AA2', 3, 50, '基础模块', 'basicset', 5, '生产项目', 'projectdata', 1, 1, '查看', 'projectdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-06-27 14:30:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-06-27 14:32:43', 1),"
							+ "('C9E0C89EA0200001B9EA7F52148089D0', 3, 50, '基础模块', 'basicset', 5, '生产项目', 'projectdata', 1, 2, '新增', 'projectdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-06-27 14:32:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-06-27 14:32:45', 1),"
							+ "('C9E0C8A1E5400001F24FAA836FA71F17', 3, 50, '基础模块', 'basicset', 5, '生产项目', 'projectdata', 1, 3, '删除', 'projectdata:delete', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-06-27 14:32:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-06-27 14:32:47', 1),"
							+ "('C9E308E5520000011FFD1182520013C8', 3, 50, '基础模块', 'basicset', 85, '工序管理', 'new_stepmanage', 1, 11, '导出数据', 'new_stepmanage:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-04 14:23:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-04 14:24:16', 1),"
							+ "('C9E308EF62800001128859301B81C470', 3, 50, '基础模块', 'basicset', 85, '工序管理', 'new_stepmanage', 1, 10, '批量变更计工方式', 'new_stepmanage:changemode', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-04 14:24:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-04 14:24:02', 1),"
							+ "('C9E7E725E3F000012FEA14001B204230', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 30, '显示物料Bom单价', 'iteminfodata:bomshowprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-19 17:23:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-19 17:26:50', 1),"
							+ "('C9EA223F949000019C24185013CD6380', 3, 25, '生产管理', 'orderset', 1, '排产单管理', 'scheduleorderdata', 1, 17, '导入备货排产', 'scheduleorderdata:importdata', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-26 15:43:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-07-26 15:44:15', 1),"
							+ "('C9F370F49C90000161C625AA11F04AD0', 3, 25, '生产管理', 'orderset', 13, '工序外协商品查询', 'orderstageoutdata', 1, 3, '查看单价', 'orderstageoutdata:showprice', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-08-24 13:44:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-08-24 13:45:16', 1),"
							+ "('C9F7A750742000011AAD19001EE982B0', 3, 1, 'App端', 'appdata', 11, '工序变更', 'stepchange', 1, 1, '保存', 'stepchange:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-06 15:50:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-06 15:52:29', 1),"
							+ "('C9F7A76B0580000129D3A09DD0EAD590', 3, 1, 'App端', 'appdata', 11, '工序变更', 'stepchange', 1, 1, '查看', 'stepchange:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-06 15:52:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-06 15:52:12', 1),"
							+ "('C9FB9423AA4000014B3E3BA8AC706240', 3, 25, '生产管理', 'orderset', 40, '工序生产汇总', 'summarystepdata', 1, 2, '导出数据', 'summarystepdata:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-18 20:30:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-18 20:31:41', 1),"
							+ "('C9FEA8095EA000012BF21AF01B9CBBF0', 3, 25, '生产管理', 'orderset', 61, '生产日报管理表', 'reportdatmanagedata', 1, 5, '审核', 'reportdatmanagedata:audit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-28 10:00:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-28 10:00:55', 1),"
							+ "('C9FEA8202E00000171601E2D12121669', 3, 20, '销售模块', 'storeoutmodel', 20, '销售退货管理', 'storeoutindata', 1, 10, '查看成本', 'storeoutindata:showcost', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-28 10:02:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-28 10:04:54', 1),"
							+ "('C9FEA842E1800001F276184131201523', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 11, '查看成本', 'storeoutdata:showcost', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-28 10:04:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-28 10:04:35', 1),"
							+ "('C9FEF8A5254000015CC41DAB100A101A', 3, 25, '生产管理', 'orderset', 61, '生产日报管理表', 'reportdatmanagedata', 1, 6, '导出数据', 'reportdatmanagedata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-29 09:29:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-29 09:29:37', 1),"
							+ "('C9FF5B244FA00001A01313D0AC101244', 3, 25, '生产管理', 'orderset', 85, '生产汇总分析', 'reportbuildsheet', 1, 2, '导出数据', 'reportbuildsheet:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-30 14:10:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-30 14:10:51', 1),"
							+ "('CA01415B56E00001E67A13371110FF00', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 31, '导入商品工艺配置', 'iteminfodata:importitemstep', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-06 11:47:48', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-06 11:48:28', 1),"
							+ "('CA014165A1500001312993B2EA3A135C', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 32, '导出商品工艺配置', 'iteminfodata:exportitemstep', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-06 11:48:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-06 11:48:49', 1),"
							+ "('CA014A1A37B000011796C320ED009450', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 33, '导出BOM数据', 'iteminfodata:exportbomdetail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-06 14:20:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-06 14:21:03', 1),"
							+ "('CA033A1C6BC000012BF7E94018C11188', 3, 25, '生产管理', 'orderset', 11, '工单需领料查询', 'prodtataldata', 1, 2, '导出数据', 'prodtataldata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-12 14:49:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-12 14:50:32', 1),"
							+ "('CA033A2D8AC00001239ED1B71B411030', 3, 25, '生产管理', 'orderset', 11, '工单需领料查询', 'prodtataldata', 1, 1, '查看', 'prodtataldata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-12 14:50:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-12 14:50:11', 1),"
							+ "('CA049F45442000015D9B15D05FE01609', 3, 1, 'App端', 'appdata', 7, '派工任务', 'orderjobs', 1, 1, '查看', 'orderjobs:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-16 22:50:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-16 22:51:24', 1),"
							+ "('CA060C3011000001BB2F700015FE1EFF', 3, 25, '生产管理', 'orderset', 50, '工艺工单汇总', 'summarysteporderdata', 1, 3, '后端报工', 'summarysteporderdata:report', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 09:08:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 09:08:31', 1),"
							+ "('CA060C34C0300001F85B13C082001C8F', 3, 25, '生产管理', 'orderset', 50, '工艺工单汇总', 'summarysteporderdata', 1, 4, '工序变更', 'summarysteporderdata:changestep', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 09:08:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 09:09:10', 1),"
							+ "('CA06206171D00001BF62208BB18012FA', 3, 10, '采购模块', 'storeinmodel', 10, '采购入库管理', 'storeindata', 1, 20, '保存', 'storeindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:01:07', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:07', 1),"
							+ "('CA06207487D000013ADFE0D0D3001C50', 3, 10, '采购模块', 'storeinmodel', 20, '采购退货管理', 'storeinoutdata', 1, 20, '保存', 'storeinoutdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:02:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:10', 1),"
							+ "('CA06209824F00001B399184016D0A210', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 20, '变更单据状态', 'storeoutdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:04:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:12', 1),"
							+ "('CA06209F15E00001E74C1CD9D1F016A8', 3, 20, '销售模块', 'storeoutmodel', 20, '销售退货管理', 'storeoutindata', 1, 20, '保存', 'storeoutindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:05:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:17', 1),"
							+ "('CA0620A68DE0000183F7FFA2134A64C0', 3, 20, '销售模块', 'storeoutmodel', 20, '送货管理', 'deliverdata', 1, 20, '变更单据状态', 'deliverdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:05:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:20', 1),"
							+ "('CA0620C328500001BF7355A01B10D700', 3, 28, '委外加工', 'outsourcingmodel', 10, '加工出库管理', 'processoutdata', 1, 20, '变更单据状态', 'processoutdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:07:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:24', 1),"
							+ "('CA0620CC67D00001138C5AFB184311DD', 3, 28, '委外加工', 'outsourcingmodel', 20, '加工退料管理', 'processindata', 1, 9, '保存', 'processindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:08:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:27', 1),"
							+ "('CA0620E2DB4000016B9B1DCD1A1DE160', 3, 28, '委外加工', 'outsourcingmodel', 30, '加工入库管理', 'outsourcingindata', 1, 20, '保存', 'outsourcingindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:09:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:30', 1),"
							+ "('CA0620E74D500001D0EC1F2F29861B1D', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 20, '保存', 'outsourcingoutdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:10:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:41', 1),"
							+ "('CA0620EDE3500001CFBE88F9D290BD80', 3, 30, '库存模块', 'storemodel', 2, '生产领用管理', 'prodrequisitiondata', 1, 20, '保存', 'prodrequisitiondata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:10:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:44', 1),"
							+ "('CA0620F530C0000187506E8014C01347', 3, 30, '库存模块', 'storemodel', 3, '生产退料管理', 'prodrequisitionbackdata', 1, 20, '保存', 'prodrequisitionbackdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:11:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:54:45', 1),"
							+ "('CA0620F9F1300001B4B5636015663770', 3, 30, '库存模块', 'storemodel', 5, '其他入库管理', 'otherindata', 1, 20, '保存', 'otherindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:11:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:01', 1),"
							+ "('CA0621056D400001EEBB1FEC16102E70', 3, 30, '库存模块', 'storemodel', 10, '调拨管理', 'storemovedata', 1, 20, '保存', 'storemovedata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:12:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:07', 1),"
							+ "('CA06210EF54000017FBF1C2BAB595330', 3, 30, '库存模块', 'storemodel', 20, '盘点管理', 'storecheckdata', 1, 20, '保存', 'storecheckdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:12:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:10', 1),"
							+ "('CA06211365300001BAFD124D67EC3FE0', 3, 30, '库存模块', 'storemodel', 30, '组装拆卸管理', 'splitdata', 1, 3, '保存', 'splitdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:13:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:13:23', 1),"
							+ "('CA062115E130000181B3E04719005440', 3, 30, '库存模块', 'storemodel', 40, '报损记录管理', 'reportlossdata', 1, 20, '保存', 'reportlossdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:13:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:14', 1),"
							+ "('CA062119B4B000013BB71D0013C212A0', 3, 30, '库存模块', 'storemodel', 50, '生产入库管理', 'prodstoragedata', 1, 20, '保存', 'prodstoragedata:save', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:13:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:16', 1),"
							+ "('CA06216B429000011AA8C8A019401DF0', 3, 30, '库存模块', 'storemodel', 6, '其他出库管理', 'otheroutdata', 1, 20, '保存', 'otheroutdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:19:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:04', 1),"
							+ "('CA0621A203A0000148E1132014001EAE', 3, 50, '基础模块', 'basicset', 70, '期初库存管理', 'itembegindata', 1, 20, '保存', 'itembegindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-21 15:23:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-10-22 22:55:27', 1),"
							+ "('CA09A02D4EC000012194F0C0DFAE8370', 3, 5, '统一单据权限', 'allroles', 1, '有【详情】权限', 'allroles', 1, 1, '修改备注与原单号（非暂存状态）', 'allroles:modifybill', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-11-01 11:56:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-11-01 22:21:21', 1),"
							+ "('CA0B4997D180000163EE4DD09F508AB0', 3, 5, '统一单据权限', 'allroles', 2, '不受用户配置限制', 'alldatas', 1, 1, '查看全部仓库数据', 'alldatas:hasstore', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-11-06 15:50:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-11-06 15:53:17', 1),"
							+ "('CA0B49B9D1E00001D1BB7B0D12CD4000', 3, 5, '统一单据权限', 'allroles', 2, '不受用户配置限制', 'alldatas', 1, 2, '查看全部往来单位数据', 'alldatas:hascustomer', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-11-06 15:53:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-11-06 15:53:41', 1)");

					ps.addBatch("ALTER TABLE `prodrequisition_work_total` CHANGE COLUMN `scheduleid` `scheduleid` VARCHAR(36) NULL DEFAULT '' COMMENT '排产单ID' AFTER `goods_number`, CHANGE COLUMN `salesorderid` `salesorderid` VARCHAR(36) NULL DEFAULT '' COMMENT '销售订单ID' AFTER `scheduleid`,CHANGE COLUMN `remark` `remark` VARCHAR(200) NULL DEFAULT '' COMMENT '需求备注' AFTER `needcount`");

					ps.addBatch("ALTER TABLE `t_orderstep_staff` ADD COLUMN `progress_id` VARCHAR(36) NOT NULL DEFAULT '' COMMENT 'progress_id' AFTER `staffid`, ADD INDEX `progress_id` (`progress_id`)");
					ps.addBatch("update t_orderstep_staff ts set ts.progress_id = ifnull((select t.id from t_progress t where t.detail_id=ts.detail_id and t.step_id=ts.stepid limit 1),'') where ts.progress_id=''");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `timebit` INT(11) NULL DEFAULT '2' COMMENT '工时小数点位数' AFTER `moneybit`,ADD COLUMN `imageleft` INT NULL DEFAULT '0' COMMENT '单据图标离左边像素' AFTER `reportlimit`,ADD COLUMN `outstepset` INT(1) NULL DEFAULT '0' COMMENT '允许发外工序进行内部工单扫描报工' AFTER `imageleft`");
					ps.addBatch("create or replace view s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("update r_mainreturn r set r.orderstepids=ifnull((select group_concat(tp.id) from t_progress tp where r.detail_id=tp.detail_id and find_in_set(tp.step_id,r.orderstepids)),r.orderstepids) where r.orderstepids<>'' and r.finishtype<=1");
					ps.addBatch("update r_mainreturn r set r.detailstepids=ifnull((select group_concat(tp.id) from t_progress tp where r.detail_id=tp.detail_id and find_in_set(tp.step_id,r.detailstepids)),r.detailstepids) where r.detailstepids<>'' and r.finishtype<=1");

					// ps.addBatch("update s_permission  s set s.functionname='工序生产汇总' where s.functionvalue='summarystepdata'");

					ps.addBatch("ALTER TABLE `t_progress` ADD INDEX `step_no` (`step_no`),ADD INDEX `class_type` (`class_type`),ADD INDEX `isfinish` (`isfinish`)");
					ps.addBatch("update t_progress tp ,s_company_config sc set tp.finishcount=round(tp.progress_count-(tp.freturn_count-tp.return_count),sc.countbit) where tp.companyid=sc.company_id");
					ps.addBatch("update t_progress t set t.isfinish=1 where t.work_count<=t.finishcount+t.invalid_count and t.isfinish=0");

					// ps.addBatch("update t_progress tp join (select  tpm.detail_id,tpm.step_no,ifnull((select sum(tps.invalid_count/tps.progress_scale) from t_progress tps where tps.detail_id=tpm.detail_id and  tps.step_no<tpm.step_no ),0) as pinvalid_count from t_progress tpm where  tpm.class_type=1 and tpm.step_no>1) k on tp.detail_id=k.detail_id and tp.step_no=k.step_no,s_company_config sc set tp.isfinish=if(tp.work_count>round(tp.finishcount+tp.invalid_count+k.pinvalid_count,sc.countbit),0,1)  where tp.companyid=sc.company_id and tp.class_type=1 and tp.step_no>1");

					// ps.addBatch("update t_progress tp join (select  tpm.detail_id,tpm.step_no,ifnull((select sum(tps.invalid_count) from t_progress tps where tps.detail_id=tpm.detail_id and  tps.step_no<tpm.step_no ),0) as pinvalid_count from t_progress tpm where  tpm.class_type=1 and tpm.step_no>1) k on tp.detail_id=k.detail_id and tp.step_no=k.step_no set tp.isfinish=if(tp.work_count<=tp.finishcount+tp.invalid_count+k.pinvalid_count,1,0)  where tp.class_type=1 and tp.step_no>1");

					ps.addBatch("update t_progress tp  set tp.finish_time=if(tp.progress_type=2,(select max(sd.create_time) from stageoutsourcingdetail sd where sd.progressid=tp.id and sd.stype='321' and sd.status!=2),(select max(t.create_date) from t_order_progress t where t.progress_id=tp.id))");

					ps.addBatch("ALTER TABLE `t_order_detail` ADD COLUMN `t_isfinish` INT(1) NOT NULL DEFAULT '0' COMMENT '生产状态' AFTER `t_invailcount`, ADD INDEX `t_isfinish` (`t_isfinish`)");
					ps.addBatch("update t_order_detail td,s_company_config sc set td.t_isfinish=if(td.item_count<=round(td.t_finishcount+td.t_invailcount,sc.countbit),1,0) where td.companyid=sc.company_id");

					ps.addBatch("update s_permission  s set s.fstatus=2 where s.functionvalue in ('reportdaysheet','reportdaydata','reportbuilddata','stepprogressboard','summarystepdata','reportinvaliddata')");

					ps.addBatch("delete  from s_permission where fstatus=2");
					ps.addBatch("delete from s_roles_permission  where if((select 1 from s_permission s where s.id=s_roles_permission.functionid limit 1),false,true)");

					ps.addBatch("update s_permission  s set s.pseq=26,s.parentname='生产报表',s.parentvalue='orderreportset' where s.functionvalue in ('prodtataldata','summaryorderdata','orderstepdata','summarysteporderdata','summaryordersteptimedata','reportbuildsheet','staffwagesummary','orderstageoutdata')");

					ps.addBatch("update s_permission  s set s.fseq=70 where s.functionvalue='staffwagesummary'");

					ps.addBatch("update t_order_detail set class_id='102' where scheduletype=3 and class_id<>''");
					ps.addBatch("update t_progress set class_id='102' where stype=1");

					ps.addBatch("update t_order tr,s_company_config sc set  tr.canincount=round(tr.finishcount-tr.incount,sc.countbit) where tr.companyid=sc.company_id and tr.finishcount>tr.incount");

				}

				if (version < 2.01 && newversion >= 2.01) {
					ps.addBatch("update t_order set project='' where project='null'");
					ps.addBatch("update scheduleorder s set s.project = (select t.project from t_order t where t.scheduleid=s.scheduleid limit 1) where s.project='*'");

				}

				if (version < 2.02 && newversion >= 2.02) {
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `createstep` INT(1) NULL DEFAULT '1' COMMENT '允许关于导入、创建、修改工艺的工序时可直接创建新工序数据' AFTER `outstepset`");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA16CDAB21400001C4E01621DD101D9E', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 17, '作废报工', 'orderdata:invalidreport', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-12-12 10:32:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-12-12 10:32:48', 1),"
							+ "('CA17314EDF100001641F1B6017D9119E', 3, 26, '生产报表', 'orderreportset', 50, '工艺工单汇总', 'summarysteporderdata', 1, 5, '作废报工', 'summarysteporderdata:invalidreport', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-12-13 15:33:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-12-13 15:34:19', 1)");
				}

				if (version < 2.03 && newversion >= 2.03) {
					ps.addBatch("update purchase set bill_type=28 where bill_type=14");
					ps.addBatch("update purchasedetail set stype=281 where stype=141");
					ps.addBatch("ALTER TABLE `uploadimage` ADD COLUMN `companyid` VARCHAR(36) NULL DEFAULT NULL AFTER `order_progressid`, ADD INDEX `companyid` (`companyid`), ADD INDEX `scan_type` (`scan_type`)");
					ps.addBatch("update uploadimage u,s_userinfo s set u.companyid=s.companyid where u.create_id=s.userid");

					ps.addBatch("update customermonth c,s_company_config s set c.rec_add_money=round(c.rec_sellout_money-c.rec_sellin_money,s.moneybit) where c.companyid=s.company_id and c.rec_add_money<>round(c.rec_sellout_money-c.rec_sellin_money,s.moneybit)");

					ps.addBatch("update customermonth c,s_company_config s set  c.receivable=round(c.rec_add_money-c.rec_money-c.rec_cmoney,s.moneybit) where c.companyid=s.company_id and c.receivable<>round(c.rec_add_money-c.rec_money-c.rec_cmoney,s.moneybit)");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA1E8A96ADD000012B27F5BEA4A066C0', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 22, '排产', 'salesorderdata:scheduling', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-01-05 11:31:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-01-05 11:31:42', 1)");

					ps.addBatch("update r_mainreturn r set r.orderstepids=ifnull((select group_concat(tp.id) from t_progress tp where r.detail_id=tp.detail_id and find_in_set(concat('，',tp.step_name,'，'),concat('，',r.orderstepnames,'，'))),r.orderstepids) where r.orderstepids='' and r.orderreturncount>0");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `showprinttip` INT(1) NULL DEFAULT '0' COMMENT '单据保存成功后，可直接调取单据打印功能' AFTER `createstep`,ADD COLUMN `productinset` INT(1) NULL DEFAULT '0' COMMENT '跟工单生产入库必需领料才能入库' AFTER `showprinttip`,ADD COLUMN `outsourcinginset` INT(1) NULL DEFAULT '0' COMMENT '跟委外单的加工入库必需出料才能入库' AFTER `productinset`");
					ps.addBatch("create or replace view s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("update salesorder set stockstatus='0' where stockstatus='3'");
					ps.addBatch("update salesorderdetail set schedulstatus='0' where schedulstatus='3'");

					ps.addBatch("update scheduleorder s ,s_company_config sc set s.count = round((select ifnull(sum(t.order_count),0) from t_order t where t.scheduleid=s.scheduleid),sc.countbit) where s.companyid=sc.company_id and s.count=0");

					ps.addBatch("update salesorderdetail s,s_company_config sc set s.scheduledcount = round(ifnull((select sum(order_count) from t_order where salesorderdetailid=s.detailid and order_status<>2),0),sc.countbit)   where s.companyid=sc.company_id and s.`status`>0 and s.scheduledcount>0 and s.schedulcount=0 ");

				}

				if (version < 2.04 && newversion >= 2.04) {
					ps.addBatch("ALTER TABLE `customer` ADD COLUMN `iscanuse` INT(1) NULL DEFAULT '2' COMMENT '客户端访问' AFTER `usercount`,ADD COLUMN `visitno` VARCHAR(50) NULL DEFAULT '' COMMENT '访问号' AFTER `iscanuse`,ADD COLUMN `visitdate` DATETIME NULL DEFAULT NULL COMMENT '生成日期' AFTER `visitno`,ADD COLUMN `visitrole` VARCHAR(50) NULL DEFAULT '' COMMENT '访问权限' AFTER `visitdate`");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `visitcolor` VARCHAR(10)  NULL DEFAULT '#367fa9' COMMENT '访问端表头颜色' AFTER `outsourcinginset`, ADD COLUMN `visititle` VARCHAR(100)  NULL DEFAULT '订单跟踪进度' COMMENT '访问端表头' AFTER `visitcolor`, ADD COLUMN `visitprinttitle` VARCHAR(50)  NULL DEFAULT '订单访问号' COMMENT '订单访问号' AFTER `visititle`,ADD COLUMN `visiturl` VARCHAR(100) NULL DEFAULT '' COMMENT '访问地址' AFTER `visitprinttitle`,ADD COLUMN `visitmonth` INT NULL DEFAULT '6' COMMENT '访问月份' AFTER `visiturl`,ADD COLUMN `visithasno` INT(1) NULL DEFAULT '1' COMMENT '访问地址含客户号' AFTER `visitmonth`,ADD COLUMN `visitexport` INT(1) NULL DEFAULT '1' COMMENT '导出订单数据' AFTER `visithasno`"
							+ ",ADD COLUMN `visitproperty` INT(1) NULL DEFAULT '1' COMMENT '显示商品属性' AFTER `visitexport`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA279F67FDE00001DC47D98515D11E9F', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 21, '查看客户端访问信息', 'customerdata:visitshow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 16:40:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:37:03', 1),"
							+ "('CA279F8FF5B00001E3681D001B4215FC', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 26, '打印客户端访问二维码', 'customerdata:visitprint', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 16:43:17', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:37:26', 1),"
							+ "('CA279FAE05B0000127E61740D5A01169', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 22, '批量启/停客户端访问', 'customerdata:visitcanuse', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 16:45:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:37:06', 1),"
							+ "('CA279FC87C50000135E61D401D821D2C', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 23, '批量生成客户端访问号', 'customerdata:visitnocreate', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 16:47:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:37:11', 1),"
							+ "('CA279FF358F000011D5FD02019708250', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 24, '批量变更访问权限', 'customerdata:visitroleset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 16:50:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:37:13', 1),"
							+ "('CA27A048F8200001E5677A70D7801965', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 25, ' 导出客户端访问二维码', 'customerdata:visitexport', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 16:55:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:37:19', 1),"
							+ "('CA27A29AD9E00001E91FB7B0CB31194A', 4, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 20, '客户端访问配置', 'customerdata:visitset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:36:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-02-02 17:36:52', 1)");

					ps.addBatch("update itemstep ip,t_step ts set ip.step_id=ts.id where ip.class_id=ts.class_id and ip.stepnewid=ts.stepnewid and ip.step_no=ts.step_no and ip.step_id=''");

					ps.addBatch("update t_progress ip,t_step ts set ip.step_id=ts.id where ip.class_id=ts.class_id and ip.stepnewid=ts.stepnewid and ip.step_no=ts.step_no and ip.step_id=''");

					ps.addBatch("update t_stepnew set s_work_mode=1 where s_work_mode is null");

					ps.addBatch("update t_progress set start_time =(select min(if(begintime is null,create_date,begintime)) from t_order_progress where progress_id=t_progress.id and fstatus=1) where start_time is not null");// 2.05也需执行一次

				}

				if (version < 2.05 && newversion >= 2.05) {
					ps.addBatch("update outsourcingdetail od,outsourcing o set od.operate_time=o.operate_time where od.outsourcingid=o.outsourcingid and od.operate_time is null");

					ps.addBatch("update stock o set o.t_sellcount=ifnull((select sum(s.count) from storeoutdetail s where s.stype='71' and s.companyid=o.companyid and s.itemid=o.itemid and o.houseid=s.houseid and  s.batchno=o.batchno and s.status='1'),0),o.t_sellmoney=ifnull((select sum(s.total) from storeoutdetail s where s.stype='71' and s.companyid=o.companyid and s.itemid=o.itemid and o.houseid=s.houseid and  s.batchno=o.batchno and s.status='1'),0),o.t_sellcost=ifnull((select sum(s.cost_money) from storeoutdetail s where s.stype='71'  and s.companyid=o.companyid and s.itemid=o.itemid and o.houseid=s.houseid and  s.batchno=o.batchno and s.status='1'),0)");
					ps.addBatch("update stock s,s_company_config sc set   count= round(ifnull(`totalcount`,0)+ `begincount`- `t_totalcount`- ifnull(`sellcount`,0)+ ifnull(`t_sellcount`,0)+`profitcount`- `losscount`+`in_count`-`out_count`+ `splitsin_count`- `splitsout_count`- `prodreq_count`+ `prodstorage_count`+prodreqback_count+ `otherin_count`- `otherout_count`+outsourcing_count-processout_count+processin_count,sc.countbit),money=  round(ifnull(`totalmoney`,0)+`begintotal`-`t_totalmoney`- ifnull(`sellcost`,0) + `t_sellcost`+ `profitmoney`- `lossmoney`+ `in_money`- `out_money`+ `splitsin_money`- `splitsout_money`- `prodreq_money` + `prodstorage_money`+ `otherin_money`- `otherout_money`+prodreqback_money+outsourcing_cost-processout_money+processin_money,sc.moneybit) ,newcostprice=round(if(count > 0,money / count , 0),sc.pricebit) where  s.companyid=sc.company_id");

					ps.addBatch("update stock s set s.newcostprice=0 where s.newcostprice<0 and s.count=0");

					ps.addBatch("update t_progress t,t_step tp set t.stepnewid=tp.stepnewid  where t.step_id=tp.id and  t.step_id<>'' and  t.stepnewid='null'");
					ps.addBatch("update t_progress t,t_stepnew tp set t.stepnewid=tp.stepnewid  where t.companyid=tp.companyid and t.step_name=tp.stepnewname  and  t.stepnewid='null'");
					ps.addBatch("update t_order_progress tp,t_progress t set tp.stepnewid=t.stepnewid where tp.progress_id=t.id and tp.stepnewid='null'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA526F305BC00001429CF400570F1393', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 24, '导出生产工序情况', 'salesorderdata:exportstep', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-06-15 16:56:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-06-15 16:58:30', 1),"
							+ "('CA526F4562C00001B028A8711FF01162', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 23, '查看领料明细', 'salesorderdata:prodrequisition', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-06-15 16:58:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-06-15 16:58:04', 1)");

					ps.addBatch("ALTER TABLE `storetemplate` ADD COLUMN `showimg` INT(1) NOT NULL DEFAULT '0' COMMENT '是否显示图片列' AFTER `Afax`");
					ps.addBatch("delete from s_roles_permission where functionid='C943DAC4A7C00001A9D41BC0C81C6490'");
					ps.addBatch("delete from s_permission where id='C943DAC4A7C00001A9D41BC0C81C6490'");
					ps.addBatch("update s_permission set fseq=85  where fvalue='new_stepmanage:new' or fvalue='new_stepmanage:status'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES"
							+ "('CA38AF62B1500001B697154C24401A66', 3, 10, '采购模块', 'storeinmodel', 10, '采购入库管理', 'storeindata', 1, 21, '箱单打印', 'storeindata:boxprint', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-03-27 16:56:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-03-27 16:58:04', 1),"
							+ "('CA38AF8338400001B4131E0D74B8FE20', 3, 30, '库存模块', 'storemodel', 50, '生产入库管理', 'prodstoragedata', 1, 21, '箱单打印', 'prodstoragedata:boxprint', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-03-27 16:58:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-03-28 10:37:53', 1),"
							+ "('CA38AF8B96F00001355C109B116C1EC7', 3, 28, '委外加工', 'outsourcingmodel', 30, '加工入库管理', 'outsourcingindata', 1, 21, '箱单打印', 'outsourcingindata:boxprint', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-03-27 16:59:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-03-28 10:37:17', 1),"
							+ "('CA3C230075600001D0201F503FB38FB0', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 23, '发货箱单打印', 'salesorderdata:boxprint', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-04-07 10:18:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-04-07 10:19:14', 1),"
							+ "('CA5AB5440CD00001B9D119103910EC60', 3, 1, 'App端', 'appdata', 2, '工单扫描', 'orderscan', 1, 3, '产品标签打印(windows或IOS)', 'orderscan:productprint', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-11 09:52:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-11 09:53:10', 1),"
							+ "('CA5AB55A2A300001F05C1F621610120C', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 9, '商品码打印(windows或IOS)', 'itemsearch:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-11 09:54:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-11 09:54:49', 1)");

					ps.addBatch("delete from t_order_progress   where if((select 1 from t_order tor where t_order_progress.order_id=tor.id limit 1),false,true)");
					ps.addBatch("delete from t_detail_code  where if((select 1 from t_order tor where t_detail_code.order_id=tor.id limit 1),false,true)");
					ps.addBatch("delete from t_detail_progress  where if((select 1 from t_order tor where t_detail_progress.order_id=tor.id limit 1),false,true)");
					ps.addBatch("delete from t_progress   where if((select 1 from t_order tor where t_progress.order_id=tor.id limit 1),false,true)");
					ps.addBatch("delete from t_order_detail   where if((select 1 from t_order tor where t_order_detail.order_id=tor.id limit 1),false,true)");
					ps.addBatch("delete from t_file   where fstatus=1 and if((select 1 from t_order_detail tor where t_file.detail_id=tor.id limit 1),false,true)");

					ps.addBatch("update stock sk,s_company_config sc set sk.checkout_count=round(ifnull((select sum(sd.count) from storeoutdetail sd where sk.itemid=sd.itemid and sk.houseid=sd.houseid and sk.batchno=sd.batchno and sd.stype='21' and sd.`status`='3'),0)+ifnull((select sum(sd.count) from processinoutdetail sd where sk.itemid=sd.itemid and sk.houseid=sd.houseid and sk.batchno=sd.batchno and sd.stype='231' and sd.`status`='3'),0),sc.countbit)  where sk.companyid=sc.company_id and sk.checkout_count<>0");
					ps.addBatch("update salesorderdetail sk,s_company_config sc set sk.checkout_count=round(ifnull((select sum(sd.count) from storeoutdetail sd where sk.detailid=sd.relationdetailid and sd.stype='21' and sd.`status`='3'),0),sc.countbit)  where sk.companyid=sc.company_id and sk.checkout_count<>0");

				}

				if (version < 2.06 && newversion >= 2.06) {
					ps.addBatch("update outsourcingdetail o set o.enddate=(select max(DATE_FORMAT(d.create_time,'%Y-%m-%d')) from outsourcingindetail d where d.status='1' and d.relationdetailid=o.detailid) where  o.enddate is not null");

					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `showadv` VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '0不显示 1显示' AFTER `checkLG`, ADD COLUMN `advtime` INT(11) NOT NULL DEFAULT '30' COMMENT '弹出广告间隔时间' AFTER `showadv`, ADD COLUMN `limitcomfilesize` VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '0不限制 1限制' AFTER `advtime`, ADD COLUMN `comfilesize` INT(11) NOT NULL DEFAULT '0' COMMENT '0不限制 默认空间大小' AFTER `limitcomfilesize`");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `abvlimitshow` INT(1) NULL DEFAULT '-1' COMMENT '-1免广告 1限免广告' AFTER `visitproperty`, ADD COLUMN `abvenddate` DATE NULL DEFAULT NULL COMMENT '免广告期至' AFTER `abvlimitshow`, ADD COLUMN `comfilesize` INT(11) NULL DEFAULT '0' COMMENT '可上传空间大小MB' AFTER `abvenddate`, ADD COLUMN `uploadcomfilesize` INT(11) NULL DEFAULT '0' COMMENT '已上传空间大小B' AFTER `comfilesize`");
					ps.addBatch("ALTER TABLE `t_fee_param` ADD COLUMN `feetype` INT(1) NULL DEFAULT '1' COMMENT '类型' AFTER `amount`, ADD COLUMN `uploadsize` INT(11) NULL DEFAULT '0' COMMENT '上传大小' AFTER `feetype`");
					ps.addBatch("ALTER TABLE `t_file` ADD COLUMN `filesize` INT(11) NULL DEFAULT '0' COMMENT '文件大小' AFTER `fstatus`");
					ps.addBatch("ALTER TABLE `uploadimage` ADD COLUMN `filesize` INT(11) NULL DEFAULT '0' COMMENT '文件大小' AFTER `create_by`");
					ps.addBatch("ALTER TABLE `iteminfo` ADD COLUMN `filesize` INT(11) NULL DEFAULT '0' COMMENT '文件大小' AFTER `hasstep`");
					ps.addBatch("ALTER TABLE `itemfile` ADD COLUMN `filesize` INT(11) NULL DEFAULT '0' COMMENT '文件大小' AFTER `create_time`");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `adv` (  `id` varchar(36) NOT NULL COMMENT '编号',  `comname` varchar(150) NOT NULL DEFAULT '' COMMENT '广告所属公司', `fstatus` int(1) NOT NULL DEFAULT '1' COMMENT '状态',  `advname` varchar(200) NOT NULL DEFAULT '' COMMENT '广告名称', `advtype` int(1) NOT NULL DEFAULT '1' COMMENT '广告类型', `advurl` varchar(200) NOT NULL DEFAULT '' COMMENT '广告链接', `advclosetime` int(11) NOT NULL DEFAULT '60' COMMENT '多少秒后显示关闭', `level` int(11) NOT NULL DEFAULT '0' COMMENT '广告显示优先级',  `remark` varchar(300) NOT NULL DEFAULT '' COMMENT '广告备注', `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',  `create_date` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',  `update_date` datetime DEFAULT NULL COMMENT '更新时间', PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("ALTER TABLE `s_company_severlife` ADD COLUMN `stype` INT(1) NULL DEFAULT '1' COMMENT '1公司 2去广告' AFTER `feeid`");
					ps.addBatch("ALTER TABLE `t_userfee_param` ADD COLUMN `feetype` INT(1) NULL DEFAULT '1' COMMENT '类型' AFTER `amount`, ADD COLUMN `uploadsize` INT(11) NULL DEFAULT '0' COMMENT '上传大小' AFTER `feetype`");

					ps.addBatch("update  t_progress t,t_step ts set t.stepnewid=ts.stepnewid where t.step_id=ts.id and  t.stepnewid='null'");
					ps.addBatch("update  t_order_progress t,t_step ts set t.stepnewid=ts.stepnewid where t.step_id=ts.id and  t.stepnewid='null'");

					ps.addBatch("ALTER TABLE `s_userinfo` ADD COLUMN `filesize` INT NULL DEFAULT '0' COMMENT '头像大小' AFTER `usedmoney`");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA5F8F23ED500001129614101F0011CE', 1, 100, '系统管理', 'systemset', 26, '广告管理', 'advdata', 1, 3, '删除', 'advdata:delete', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-26 11:36:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-26 11:37:07', 1),"
							+ "('CA5F8F2D4BD00001D512172110DD1D79', 1, 100, '系统管理', 'systemset', 26, '广告管理', 'advdata', 1, 1, '查看', 'advdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-26 11:36:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-26 11:36:41', 1),"
							+ "('CA5F8F2FC770000125E06E80A1F616E2', 1, 100, '系统管理', 'systemset', 26, '广告管理', 'advdata', 1, 2, '新增', 'advdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-26 11:36:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-26 14:29:11', 1)");

					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `firstadv` INT(1) NOT NULL DEFAULT '0' COMMENT '首次登录显示广告' AFTER `comfilesize`");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `firstadv` INT(1) NULL DEFAULT '0' COMMENT '公司首次登录显示广告' AFTER `comfilesize`");
					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `fileuploadday` INT(11) NOT NULL DEFAULT '-1' COMMENT '可上传文件天数' AFTER `firstadv`");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `uploadlimitshow` INT(1) NULL DEFAULT '-1' COMMENT '上传-1不限制 1限制' AFTER `firstadv`, ADD COLUMN `uploadenddate` DATE NULL DEFAULT NULL COMMENT '可上传期至' AFTER `uploadlimitshow`");
					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `advshowpay` INT(1) NOT NULL DEFAULT '0' COMMENT '广告到期是否显示收费' AFTER `firstadv`,ADD COLUMN `uploadshowpay` INT(1) NOT NULL DEFAULT '0' COMMENT '上传到期或空间不足显示收费' AFTER `advshowpay`");

					ps.addBatch("create or replace view item_batchno_stock as select i.*,ifnull(cs.classname,'') as classname ,s.houseid ,sh.housecode,sh.housename,s.batchno,ifnull(s.count,0) as count,ifnull(s.money,0) as money,ifnull(s.newcostprice,0) as newcostprice,ifnull(s.checkout_count,0) as checkout_count  from iteminfo i left join itemclass cs on i.classid=cs.classid ,stock s , storehouse sh  where i.itemid = s.itemid and s.houseid=sh.houseid and sh.status='1' ");

					ps.addBatch("update t_order_progress t,t_progress tp set t.stepnewid=tp.stepnewid where t.progress_id=tp.id and t.stepnewid<>tp.stepnewid");

					ps.addBatch("update t_order t set t.schedulestatus='2' where  t.schedulestatus='1' and ((t.incount>=t.order_count) or (t.incount>0 and t.incount=t.finishcount and (select count(td.id) from t_order_detail td where td.order_id=t.id and td.t_isfinish=0)=0))");
					ps.addBatch("update t_order_detail td set td.schedulestatus='2' where td.schedulestatus='1' and (select 1 from t_order t where t.id=td.order_id and t.schedulestatus='2' limit 1)");
					ps.addBatch("update t_progress t,t_order td set t.jobdistribution=3 where t.order_id=td.id and t.jobdistribution=0 and  td.schedulestatus>1");
					ps.addBatch("update scheduleorder s set s.schedulestatus='2' where s.schedulestatus='1' and (select count(t.id) from t_order t where t.scheduleid=s.scheduleid and t.schedulestatus='1')=0");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

				}

				if (version < 2.1 && newversion >= 2.1) {
					ps.addBatch("ALTER TABLE `purchaseorder` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `total`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`");
					ps.addBatch("ALTER TABLE `purchaseorderdetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `total`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`");
					ps.addBatch("update purchaseorderdetail p set p.taxprice=p.price,p.taxmoney=p.total where 1=1");
					ps.addBatch("update purchaseorder p set p.totalmoney=p.total where 1=1");

					ps.addBatch(" ALTER TABLE `storein` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `total`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`");
					ps.addBatch("ALTER TABLE `storeindetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `total`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`,"
							+ "ADD COLUMN `isInvoice_d` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `relationmainid`, ADD COLUMN `isEnd_d` INT(1) NULL DEFAULT '0' COMMENT '结款情况' AFTER `isInvoice_d`, ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT '' COMMENT '标记说明' AFTER `isEnd_d`");
					ps.addBatch("update storeindetail p set p.taxprice=p.price,p.taxmoney=p.total where 1=1");
					ps.addBatch("update storein p set p.totalmoney=p.total where 1=1");

					ps.addBatch("ALTER TABLE `storeout` ADD COLUMN `currency` VARCHAR(50) NULL DEFAULT '人民币' COMMENT '币种' AFTER `customerid`, ADD INDEX `currency` (`currency`)");
					ps.addBatch("ALTER TABLE `salesorder` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `total`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`,ADD COLUMN `currency` VARCHAR(50) NULL DEFAULT '人民币' COMMENT '币种' AFTER `customerid`, ADD INDEX `currency` (`currency`)");
					ps.addBatch("ALTER TABLE `salesorderdetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `total`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`");
					ps.addBatch("update salesorderdetail p set p.taxprice=p.price,p.taxmoney=p.total where 1=1");
					ps.addBatch("update salesorder p set p.totalmoney=p.total where 1=1");

					ps.addBatch("ALTER TABLE `storeout` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `total`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`");
					ps.addBatch("ALTER TABLE `storeoutdetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `total`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`,"
							+ "ADD COLUMN `isInvoice_d` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `relationmainid`, ADD COLUMN `isEnd_d` INT(1) NULL DEFAULT '0' COMMENT '结款情况' AFTER `isInvoice_d`, ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT '' COMMENT '标记说明' AFTER `isEnd_d`");
					ps.addBatch("update storeoutdetail p set p.taxprice=p.price,p.taxmoney=p.total where 1=1");
					ps.addBatch("update storeout p set p.totalmoney=p.total where 1=1");

					ps.addBatch("ALTER TABLE `outsourcing` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `total`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`");
					ps.addBatch("ALTER TABLE `outsourcingdetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `total`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`");
					ps.addBatch("update outsourcingdetail p set p.taxprice=p.price,p.taxmoney=p.total where 1=1");
					ps.addBatch("update outsourcing p set p.totalmoney=p.total where 1=1");

					ps.addBatch("ALTER TABLE `outsourcingin` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `processmoney`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`");
					ps.addBatch("ALTER TABLE `outsourcingindetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `processmoney`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`,ADD COLUMN `isInvoice_d` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `returnorderid`, ADD COLUMN `isEnd_d` INT(1) NULL DEFAULT '0' COMMENT '结款情况' AFTER `isInvoice_d`,ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT '' COMMENT '标记说明' AFTER `isEnd_d`");
					ps.addBatch("update outsourcingindetail p set p.taxprice=p.processprice,p.taxmoney=p.processmoney where 1=1");
					ps.addBatch("update outsourcingin p set p.totalmoney=p.processmoney where 1=1");

					ps.addBatch("ALTER TABLE `stageoutsourcing` ADD COLUMN `totaltax` DOUBLE NULL DEFAULT '0' COMMENT '总税额' AFTER `processmoney`,ADD COLUMN `totalmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税总额' AFTER `totaltax`,ADD COLUMN `isInvoice` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `totalmoney`, ADD COLUMN `isEnd_d` INT(1) NULL DEFAULT '0' COMMENT '结款情况' AFTER `isInvoice`");
					ps.addBatch("ALTER TABLE `stageoutsourcingdetail` ADD COLUMN `taxrate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `dprocessmoney`, ADD COLUMN `tax` DOUBLE NULL DEFAULT '0' COMMENT '税额' AFTER `taxrate`, ADD COLUMN `taxprice` DOUBLE NULL DEFAULT '0' COMMENT '含税单价' AFTER `tax`, ADD COLUMN `taxmoney` DOUBLE NULL DEFAULT '0' COMMENT '价税合计' AFTER `taxprice`,ADD COLUMN `isInvoice_d` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `relationorderid`, ADD COLUMN `isEnd_d` INT(1) NULL DEFAULT '0' COMMENT '结款情况' AFTER `isInvoice_d`, ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT '' COMMENT '标记说明' AFTER `isEnd_d`");
					ps.addBatch("update stageoutsourcingdetail p set p.taxprice=p.dprice,p.taxmoney=p.dprocessmoney where 1=1");
					ps.addBatch("update stageoutsourcing p set p.totalmoney=p.processmoney where 1=1");

					ps.addBatch("ALTER TABLE `storeoutdetail` ADD COLUMN `invoicedate` DATE NULL DEFAULT NULL COMMENT '开票日期' AFTER `operate_time`, ADD INDEX `invoicedate` (`invoicedate`)");

					ps.addBatch("ALTER TABLE `storeindetail` ADD COLUMN `invoicedate` DATE NULL DEFAULT NULL COMMENT '开票日期' AFTER `operate_time`,  ADD INDEX `invoicedate` (`invoicedate`)");

					ps.addBatch("ALTER TABLE `outsourcingindetail` ADD COLUMN `invoicedate` DATE NULL DEFAULT NULL COMMENT '开票日期' AFTER `operate_time`, ADD INDEX `invoicedate` (`invoicedate`)");

					ps.addBatch("ALTER TABLE `stageoutsourcingdetail` ADD COLUMN `invoicedate` DATE NULL DEFAULT NULL COMMENT '开票日期' AFTER `operate_time`,  ADD INDEX `invoicedate` (`invoicedate`)");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `currencylist` VARCHAR(300) NOT NULL DEFAULT '人民币；美元；欧元；英镑' COMMENT '币种列表' AFTER `uploadenddate`");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `storecontract` ADD COLUMN `partAB` INT(1) NOT NULL DEFAULT '1' COMMENT '本公司为哪方' AFTER `Afax`");
					ps.addBatch("ALTER TABLE `storetemplate` ADD COLUMN `partAB` INT(1) NOT NULL DEFAULT '1' COMMENT '本公司为哪方' AFTER `showimg`");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('C9FF4A810F400001279D70291F6095E0', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 12, '不需审核可以出库', 'storeoutdata:changestatus', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2022-09-30 09:19:48', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-18 13:53:44', 1), "
							+ "('CA5CA832A780000181A8F30015D01E70', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 13, '修改', 'storeoutdata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:12:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:12:11', 1), "
							+ "('CA5CA83947B0000155E35DA04D603390', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 14, '删除', 'storeoutdata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:12:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:12:38', 1), "
							+ "('CA5CA83FCA400001EC99A4F51A402860', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 15, '审核', 'storeoutdata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:13:05', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:13:05', 1), "
							+ "('CA5CA845C0D00001BEDC15A7BC461CF0', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 16, '反审', 'storeoutdata:reaudit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:13:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:13:29', 1), "
							+ "('CA5CA8579B000001FB901581D35EA710', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 17, '出货', 'storeoutdata:out', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:14:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-07-17 11:14:42', 1),"
							+ "('CA81FBA2114000016FC2BCB087711281', 3, 35, '财务模块', 'cashiermodel', 9, '对账管理', 'statement', 1, 5, '修改加工对账数据', 'statement:modify3', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-10 10:25:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-10 10:28:27', 1),"
							+ "('CA81FBBDD6B00001DC9111202930149B', 3, 35, '财务模块', 'cashiermodel', 9, '对账管理', 'statement', 1, 3, '修改供应商对账数据', 'statement:modify1', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-10 10:27:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-10 10:28:22', 1),"
							+ "('CA81FBC0AFB0000195C21F70A5331C61', 3, 35, '财务模块', 'cashiermodel', 9, '对账管理', 'statement', 1, 4, '修改客户对账数据', 'statement:modify2', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-10 10:27:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-10 10:28:23', 1),"
							+ "('CA8439E373D00001DBC21B001870192E', 3, 10, '采购模块', 'storeinmodel', 5, '采购订单管理', 'purchaseorderdata', 1, 20, '主附件查看', 'purchaseorderdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:41:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:42:40', 1),"
							+ "('CA8439EEE5600001955AB410B380185B', 3, 10, '采购模块', 'storeinmodel', 5, '采购订单管理', 'purchaseorderdata', 1, 19, '主附件上传', 'purchaseorderdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:42:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:42:08', 1),"
							+ "('CA8439FDA6000001BCD11D4A51C41F39', 3, 10, '采购模块', 'storeinmodel', 10, '采购入库管理', 'storeindata', 1, 23, '主附件查看', 'storeindata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:43:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:44:02', 1),"
							+ "('CA843A0005F00001B53319C017E01739', 3, 10, '采购模块', 'storeinmodel', 10, '采购入库管理', 'storeindata', 1, 22, '主附件上传', 'storeindata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:43:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:43:56', 1),"
							+ "('CA843A1818600001CDDB4CF2C1D01F95', 3, 10, '采购模块', 'storeinmodel', 20, '采购退货管理', 'storeinoutdata', 1, 22, '主附件查看', 'storeinoutdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:44:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:45:40', 1),"
							+ "('CA843A1888600001CA511EA0C72F93B0', 3, 10, '采购模块', 'storeinmodel', 20, '采购退货管理', 'storeinoutdata', 1, 21, '主附件上传', 'storeinoutdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:44:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:45:38', 1),"
							+ "('CA843A4762D00001E645B53013608550', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 26, '主附件查看', 'salesorderdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:48:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:48:48', 1),"
							+ "('CA843A479FE00001C666421F13B08F00', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 25, '主附件上传', 'salesorderdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:48:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:48:37', 1),"
							+ "('CA843A57BCB0000159731F0564201240', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 22, '主附件查看', 'storeoutdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:49:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:49:48', 1),"
							+ "('CA843A57FF300001CA5C2EA0239FC890', 3, 20, '销售模块', 'storeoutmodel', 10, '销售出库管理', 'storeoutdata', 1, 21, '主附件上传', 'storeoutdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:49:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:49:41', 1),"
							+ "('CA843A62E08000019A181714CF001365', 3, 20, '销售模块', 'storeoutmodel', 20, '销售退货管理', 'storeoutindata', 1, 22, '主附件查看', 'storeoutindata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:50:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:50:30', 1),"
							+ "('CA843A630EB0000142A57FB019A0FEE0', 3, 20, '销售模块', 'storeoutmodel', 20, '销售退货管理', 'storeoutindata', 1, 21, '主附件上传', 'storeoutindata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:50:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:50:25', 1),"
							+ "('CA843A727CC000015680C13082C5CE80', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 20, '主附件查看', 'outsourcingdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:51:07', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:51:35', 1),"
							+ "('CA843A72C0A00001EB28B7B019B51609', 3, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 19, '主附件上传', 'outsourcingdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:51:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:51:31', 1),"
							+ "('CA843A8316F00001201B49A4155B184A', 3, 28, '委外加工', 'outsourcingmodel', 30, '加工入库管理', 'outsourcingindata', 1, 23, '主附件查看', 'outsourcingindata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:52:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:52:50', 1),"
							+ "('CA843A833C80000141B154F0E6AAB6B0', 3, 28, '委外加工', 'outsourcingmodel', 30, '加工入库管理', 'outsourcingindata', 1, 22, '主附件上传', 'outsourcingindata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:52:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:52:48', 1),"
							+ "('CA843A8F65800001D51A13FB5FB01DD2', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 21, '主附件上传', 'outsourcingoutdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:53:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:53:30', 1),"
							+ "('CA843A8F9C300001F89B194E1BC03230', 3, 28, '委外加工', 'outsourcingmodel', 31, '加工退货管理', 'outsourcingoutdata', 1, 22, '主附件查看', 'outsourcingoutdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:53:07', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:53:35', 1),"
							+ "('CA843A9F2F00000155B81E4A12C9E310', 3, 28, '委外加工', 'outsourcingmodel', 10, '加工出库管理', 'processoutdata', 1, 22, '主附件查看', 'processoutdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:54:10', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:54:44', 1),"
							+ "('CA843A9F6F700001D17B51101D101B71', 3, 28, '委外加工', 'outsourcingmodel', 10, '加工出库管理', 'processoutdata', 1, 21, '主附件上传', 'processoutdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:54:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:54:38', 1),"
							+ "('CA843AB484600001A6A517F017102170', 3, 28, '委外加工', 'outsourcingmodel', 20, '加工退料管理', 'processindata', 1, 10, '主附件查看', 'processindata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:55:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:56:09', 1),"
							+ "('CA843AB4C2600001B3781310CF102160', 3, 28, '委外加工', 'outsourcingmodel', 20, '加工退料管理', 'processindata', 1, 11, '主附件上传', 'processindata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:55:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 09:56:07', 1),"
							+ "('CA843BB475B00001ABE6BEA0E67166D0', 3, 50, '基础模块', 'basicset', 80, '员工管理', 'staffdata', 1, 11, '附件查看', 'staffdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 10:13:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 10:13:53', 1),"
							+ "('CA843BB59C3000012CFC1E70A22513E8', 3, 50, '基础模块', 'basicset', 80, '员工管理', 'staffdata', 1, 10, '附件上传', 'staffdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 10:13:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-17 10:13:31', 1),"
							+ "('CA85913952300001A3B81C403DE22950', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 15, '主附件查看', 'stageoutsourcingdata:mainfileread', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:41:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:42:28', 1),"
							+ "('CA85913984A00001D8F31092997018B0', 3, 25, '生产管理', 'orderset', 14, '工序外协发货管理', 'stageoutsourcingdata', 1, 14, '主附件上传', 'stageoutsourcingdata:mainfileupload', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:41:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:42:08', 1),"
							+ "('CA85914C37A0000182501DA0BDB4189A', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 14, '主附件查看', 'stageoutsourcingindata:mainfileread', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:42:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:43:26', 1),"
							+ "('CA85914C5A2000014E49163479C0EB70', 3, 25, '生产管理', 'orderset', 15, '工序外协收货管理', 'stageoutsourcingindata', 1, 13, '主附件上传', 'stageoutsourcingindata:mainfileupload', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:42:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-21 13:43:19', 1)");

					ps.addBatch("update outsourcingindetail sd, outsourcingdetail os set sd.salesorderdetailid=os.relationdetailid,sd.salesorderid=os.relationorderid,sd.salesorderorderid=os.relationmainid where sd.relationdetailid=os.detailid");

					ps.addBatch("ALTER TABLE `customer`" + "ADD COLUMN `T_beginreceivable` DOUBLE NULL DEFAULT '0' COMMENT '期初应收款（含税）' AFTER `beginreceivable`,"
							+ "ADD COLUMN `TN_beginreceivable` DOUBLE NULL DEFAULT '0' COMMENT '期初应收款（不含税）' AFTER `T_beginreceivable`,"
							+ "ADD COLUMN `T_beginpayable` DOUBLE NULL DEFAULT '0' COMMENT '期初应付款（含税）' AFTER `beginpayable`,"
							+ "ADD COLUMN `TN_beginpayable` DOUBLE NULL DEFAULT '0' COMMENT '期初应付款（不含税）' AFTER `T_beginpayable`,"
							+ "ADD COLUMN `T_receivable` DOUBLE NULL DEFAULT '0' COMMENT '应收款（含税）' AFTER `receivable`,"
							+ "ADD COLUMN `T_payable` DOUBLE NULL DEFAULT '0' COMMENT '应付款（含税）' AFTER `payable`");

					ps.addBatch("ALTER TABLE `customerbill` ADD COLUMN `isTax` INT(1) NULL DEFAULT '0' COMMENT '是否含税' AFTER `stype`");

					ps.addBatch("ALTER TABLE `customeryear` ADD COLUMN `T_receivable` DOUBLE NULL DEFAULT '0' COMMENT '应收款(含税)' AFTER `receivable`,"
							+ "ADD COLUMN `T_payable` DOUBLE NULL DEFAULT '0' COMMENT '应付款(含税)' AFTER `payable`,"
							+ "ADD COLUMN `T_rec_sellout_money` DOUBLE NULL DEFAULT '0' COMMENT '销售金额(含税)' AFTER `rec_sellout_money`,"
							+ "ADD COLUMN `T_rec_sellin_money` DOUBLE NULL DEFAULT '0' COMMENT '销售退回金额(含税)' AFTER `rec_sellin_money`,"
							+ "ADD COLUMN `T_rec_add_money` DOUBLE NULL DEFAULT '0' COMMENT '增加应收款(含税)' AFTER `rec_add_money`,"
							+ "ADD COLUMN `T_rec_this_money` DOUBLE NULL DEFAULT '0' COMMENT '本次收款(含税)' AFTER `rec_this_money`,"
							+ "ADD COLUMN `T_rec_dis_money` DOUBLE NULL DEFAULT '0' COMMENT '收款优惠(含税)' AFTER `rec_dis_money`,"
							+ "ADD COLUMN `T_rec_money` DOUBLE NULL DEFAULT '0' COMMENT '已收款(含税)' AFTER `rec_money`,"
							+ "ADD COLUMN `T_pay_purchasein_money` DOUBLE NULL DEFAULT '0' COMMENT '采购金额(含税)' AFTER `pay_purchasein_money`,"
							+ "ADD COLUMN `T_pay_purchaseout_money` DOUBLE NULL DEFAULT '0' COMMENT '采购退回金额(含税)' AFTER `pay_purchaseout_money`,"
							+ "ADD COLUMN `T_pay_add_money` DOUBLE NULL DEFAULT '0' COMMENT '增加应付款(含税)' AFTER `pay_add_money`,"
							+ "ADD COLUMN `T_pay_this_money` DOUBLE NULL DEFAULT '0' COMMENT '本次付款(含税)' AFTER `pay_this_money`,"
							+ "ADD COLUMN `T_pay_dis_money` DOUBLE NULL DEFAULT '0' COMMENT '付款优惠(含税)' AFTER `pay_dis_money`,"
							+ "ADD COLUMN `T_pay_money` DOUBLE NULL DEFAULT '0' COMMENT '已付款(含税)' AFTER `pay_money`,"
							+ "ADD COLUMN `T_pay_outsourcing_money` DOUBLE NULL DEFAULT '0' COMMENT '委外加工费(含税)' AFTER `pay_outsourcing_money`,"
							+ "ADD COLUMN `T_rec_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应收调账(含税)' AFTER `rec_cmoney`,"
							+ "ADD COLUMN `T_pay_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应付调账(含税)' AFTER `pay_cmoney`");

					ps.addBatch("ALTER TABLE `customermonth` ADD COLUMN `T_receivable` DOUBLE NULL DEFAULT '0' COMMENT '应收款(含税)' AFTER `receivable`,"
							+ "ADD COLUMN `T_payable` DOUBLE NULL DEFAULT '0' COMMENT '应付款(含税)' AFTER `payable`,"
							+ "ADD COLUMN `T_rec_sellout_money` DOUBLE NULL DEFAULT '0' COMMENT '销售金额(含税)' AFTER `rec_sellout_money`,"
							+ "ADD COLUMN `T_rec_sellin_money` DOUBLE NULL DEFAULT '0' COMMENT '销售退回金额(含税)' AFTER `rec_sellin_money`,"
							+ "ADD COLUMN `T_rec_add_money` DOUBLE NULL DEFAULT '0' COMMENT '增加应收款(含税)' AFTER `rec_add_money`,"
							+ "ADD COLUMN `T_rec_this_money` DOUBLE NULL DEFAULT '0' COMMENT '本次收款(含税)' AFTER `rec_this_money`,"
							+ "ADD COLUMN `T_rec_dis_money` DOUBLE NULL DEFAULT '0' COMMENT '收款优惠(含税)' AFTER `rec_dis_money`,"
							+ "ADD COLUMN `T_rec_money` DOUBLE NULL DEFAULT '0' COMMENT '已收款(含税)' AFTER `rec_money`,"
							+ "ADD COLUMN `T_pay_purchasein_money` DOUBLE NULL DEFAULT '0' COMMENT '采购金额(含税)' AFTER `pay_purchasein_money`,"
							+ "ADD COLUMN `T_pay_purchaseout_money` DOUBLE NULL DEFAULT '0' COMMENT '采购退回金额(含税)' AFTER `pay_purchaseout_money`,"
							+ "ADD COLUMN `T_pay_add_money` DOUBLE NULL DEFAULT '0' COMMENT '增加应付款(含税)' AFTER `pay_add_money`,"
							+ "ADD COLUMN `T_pay_this_money` DOUBLE NULL DEFAULT '0' COMMENT '本次付款(含税)' AFTER `pay_this_money`,"
							+ "ADD COLUMN `T_pay_dis_money` DOUBLE NULL DEFAULT '0' COMMENT '付款优惠(含税)' AFTER `pay_dis_money`,"
							+ "ADD COLUMN `T_pay_money` DOUBLE NULL DEFAULT '0' COMMENT '已付款(含税)' AFTER `pay_money`,"
							+ "ADD COLUMN `T_pay_outsourcing_money` DOUBLE NULL DEFAULT '0' COMMENT '委外加工费(含税)' AFTER `pay_outsourcing_money`,"
							+ "ADD COLUMN `T_rec_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应收调账(含税)' AFTER `rec_cmoney`,"
							+ "ADD COLUMN `T_pay_cmoney` DOUBLE NULL DEFAULT '0' COMMENT '应付调账(含税)' AFTER `pay_cmoney`");

					ps.addBatch("ALTER TABLE `accountbill` ADD COLUMN `isTax` INT(1) NULL DEFAULT '0' COMMENT '是否含税' AFTER `actcustomer`");

					ps.addBatch("update customer c set c.TN_beginreceivable=c.beginreceivable,c.TN_beginpayable=c.beginpayable,c.TN_beginreceivable=c.beginreceivable,c.TN_beginpayable=c.beginpayable where 1=1");

					ps.addBatch("ALTER TABLE `accountbill` ADD COLUMN `isInvoice_d` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `isTax`, ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT '' COMMENT '标记说明' AFTER `isInvoice_d`,ADD COLUMN `invoicedate` DATE NULL DEFAULT NULL COMMENT '开票日期' AFTER `BKremark`");

					ps.addBatch("ALTER TABLE `customerbill` ADD COLUMN `isInvoice_d` INT(1) NULL DEFAULT '0' COMMENT '开票情况' AFTER `isTax`, ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT '' COMMENT '标记说明' AFTER `isInvoice_d`,ADD COLUMN `invoicedate` DATE NULL DEFAULT NULL COMMENT '开票日期' AFTER `BKremark`");

					ps.addBatch("create or replace view `accountbill_view` AS select  ab.*,si.staffcode,si.staffname,c.customercode,c.customername,a.accountname from accountbill ab left join staffinfo si on ab.operate_by=si.staffid left join customer c on ab.customerid=c.customerid left join account a on ab.accountid=a.accountid");

					ps.addBatch("update storein set iproperty='' where iproperty='null'");

					ps.addBatch("update outsourcingindetail od  set od.price=0 where od.price>0 and od.total=0");

					ps.addBatch("update s_permission set pseq=25,parentname='生产管理',parentvalue='orderset',fseq=12 where functionvalue='prodtataldata'");

					ps.addBatch("CREATE OR REPLACE  VIEW t_reportbase_v  as select  p.*,c.customercode,c.customername,o.order_count,o.max_order_count,od.item_count,od.max_item_count,od.item_remark,od.id as item_id,od.itemid,od.batchno,im.codeid,im.itemname,im.sformat,im.mcode,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,o.iproperty,s.class_id,sc.class_name,sc.id as classid,sc.fstatus as class_status,s.step_code,s.step_name,ifnull(d.device_id,'') as device_code,ifnull(d.device_name,'') as device_name,sf.staffname,sf.staffcode,ifnull(ws.workshop_id,'') as workshop_code,ifnull(ws.workshop_name,'') as workshop_name ,o.order_time,o.finish_time as forder_time,od.goods_number,o.plandate,o.order_remark,o.order_status,o.schedulestatus,o.scheduletype,o.billno,o.scheduleid,o.orderid,o.salesorderid,o.order_id as saleordercode,u.realname from t_order_progress p left join t_order o on p.order_id = o.id left join t_order_detail od on p.detail_id = od.id left join iteminfo im on im.itemid=od.itemid  left join t_step s on p.step_id = s.id  left join t_device d on p.device_id = d.id left join customer c on o.customer_id = c.customerid  left join t_step_class sc on s.class_id = sc.id  left join staffinfo sf on p.user_id = sf.staffid  left join t_workshop ws on p.workshop_id = ws.id   left join s_userinfo u on u.userid = p.create_id");

					// ALTER TABLE `storeoutdetail`
					// ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT ''
					// COMMENT '标记备注' AFTER `isEnd_d`,
					// DROP COLUMN `ledgerMark`;
					//
					// ALTER TABLE `storeindetail`
					// ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT ''
					// COMMENT '标记备注' AFTER `isEnd_d`,
					// DROP COLUMN `ledgerMark`;
					//
					// ALTER TABLE `outsourcingindetail`
					// ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT ''
					// COMMENT '标记备注' AFTER `isEnd_d`,
					// DROP COLUMN `ledgerMark`;
					//
					// ALTER TABLE `stageoutsourcingdetail`
					// ADD COLUMN `BKremark` VARCHAR(100) NULL DEFAULT ''
					// COMMENT '标记备注' AFTER `isEnd_d`,
					// DROP COLUMN `ledgerMark`;

					ps.addBatch("delete from s_permission where id='CA06209824F00001B399184016D0A210'");
					ps.addBatch("delete from s_roles_permission where functionid='CA06209824F00001B399184016D0A210'");
					ps.addBatch("update s_permission set fname='修改备注、原单号、价格（非暂存状态）' where id='CA09A02D4EC000012194F0C0DFAE8370'");
				}

				if (version < 2.2 && newversion >= 2.2) {
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_auditflow` ( `id` varchar(36) NOT NULL COMMENT '单据表名', `companyid` varchar(50) NOT NULL DEFAULT '' COMMENT '组织编号',  `fstatus` int(1) NOT NULL DEFAULT '0' COMMENT '使用状态',  `auditflownum` int(11) NOT NULL DEFAULT '0' COMMENT '序号',   `auditflowtablename` varchar(30) NOT NULL DEFAULT '' COMMENT '单据表名',  `auditflowbillname` varchar(30) NOT NULL DEFAULT '' COMMENT '单据名称',"
							+ "`flowcount` int(1) NOT NULL DEFAULT '0' COMMENT '审批流程数量',  `flow1` int(1) NOT NULL DEFAULT '0' COMMENT '流程人1', `flow1name` varchar(50) NOT NULL DEFAULT '审批人1' COMMENT '审批人1',  `flow2` int(1) NOT NULL DEFAULT '0' COMMENT '流程人2', `flow2name` varchar(50) NOT NULL DEFAULT '审批人2' COMMENT '审批人2',  `flow3` int(1) NOT NULL DEFAULT '0' COMMENT '流程人3',  `flow3name` varchar(50) NOT NULL DEFAULT '审批人3' COMMENT '审批人3',"
							+ "`flow4` int(1) NOT NULL DEFAULT '0' COMMENT '流程人4',  `flow4name` varchar(50) NOT NULL DEFAULT '审批人4' COMMENT '审批人4', `flow5` int(1) NOT NULL DEFAULT '0' COMMENT '流程人5',  `flow5name` varchar(50) NOT NULL DEFAULT '审批人5' COMMENT '审批人5',  `flow6` int(1) NOT NULL DEFAULT '0' COMMENT '流程人6',"
							+ "`flow6name` varchar(50) NOT NULL DEFAULT '审批人6' COMMENT '审批人6', `flow7` int(1) NOT NULL DEFAULT '0' COMMENT '流程人7', `flow7name` varchar(50) NOT NULL DEFAULT '审批人7' COMMENT '审批人7', `flow0` int(1) NOT NULL DEFAULT '0' COMMENT '最终流程人', `flow0name` varchar(50) NOT NULL DEFAULT '最终审批人' COMMENT '最终审批人',`isEnd` INT(1) NOT NULL DEFAULT '0' COMMENT '直接传递给终审人' ,  `remark` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '备注' , `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建ID', `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',"
							+ "`create_time` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '修改人ID',  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人',  `update_time` datetime DEFAULT NULL COMMENT '修改日期', PRIMARY KEY (`id`),  UNIQUE KEY `companyid` (`companyid`,`auditflownum`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单据审批权限'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_billflow` ( `id` varchar(36) NOT NULL COMMENT '主键',  `companyid` varchar(50) NOT NULL DEFAULT '' COMMENT '组织编号',  `billid` varchar(36) NOT NULL DEFAULT '' COMMENT '单据ID',  `orderid` varchar(50) NOT NULL DEFAULT '' COMMENT '单据编号',"
							+ "`auditflow_id` varchar(36) NOT NULL DEFAULT '' COMMENT '审核流程ID',  `flowfstatus` int(1) NOT NULL DEFAULT '1' COMMENT '流程状态', `flownum` int(1) NOT NULL DEFAULT '1' COMMENT '流程序号', `flowname` varchar(50) NOT NULL DEFAULT '' COMMENT '流程名称', `preflow` varchar(36) NOT NULL DEFAULT '' COMMENT '上一流程',  `preflownum` int(1) NOT NULL DEFAULT '1' COMMENT '上一流程序号',  `preflowname` varchar(50) NOT NULL DEFAULT '' COMMENT '上一流程名称',"
							+ "`pass_id` varchar(36) NOT NULL DEFAULT '' COMMENT '传递人ID',  `pass_by` varchar(50) NOT NULL DEFAULT '' COMMENT '传递人',  `pass_time` datetime DEFAULT NULL COMMENT '传递时间',  `pass_remark` varchar(200) DEFAULT '' COMMENT '传递说明', `receive_id` varchar(36) NOT NULL DEFAULT '' COMMENT '接收人ID', `receive_by` varchar(50) NOT NULL DEFAULT '' COMMENT '接收人', `receive_time` datetime DEFAULT NULL COMMENT '接收时间',"
							+ "`receive_remark` varchar(200) NOT NULL DEFAULT '' COMMENT '接收说明', `back_send_time` datetime DEFAULT NULL COMMENT '回退时间', `back_send_remark` varchar(200) NOT NULL DEFAULT '' COMMENT '回退说明', `back_time` datetime DEFAULT NULL COMMENT '退回接收时间',  `back_remark` varchar(200) NOT NULL DEFAULT '' COMMENT '退回接收说明', `back_type` int(1) NOT NULL DEFAULT '0' COMMENT '退回状态',  `pass_urgent` int(1) NOT NULL DEFAULT '0' COMMENT '传递紧急程度',"
							+ " `back_urgent` int(1) NOT NULL DEFAULT '0' COMMENT '回退紧急程度',  PRIMARY KEY (`id`),  KEY `companyid` (`companyid`), KEY `auditflow_id` (`auditflow_id`),  KEY `preflow` (`preflow`),  KEY `flowfstatus` (`flowfstatus`),  KEY `pass_id` (`pass_id`), KEY `receive_id` (`receive_id`),  KEY `back_type` (`back_type`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单据审批流程'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_auditflow_pespon` ( `id` varchar(36) NOT NULL COMMENT '主键', `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `auditflow_id` varchar(36) NOT NULL DEFAULT '' COMMENT '主键',   `flownum` int(1) NOT NULL DEFAULT '1' COMMENT '流程号',`ptype` INT(11) NOT NULL DEFAULT '0' COMMENT '单据序号', `userid` varchar(36) NOT NULL DEFAULT '' COMMENT '用户ID',  `username` varchar(50) NOT NULL DEFAULT '' COMMENT '登录用户', `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建ID',  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',"
							+ "`create_time` datetime DEFAULT NULL COMMENT '创建日期', PRIMARY KEY (`id`), UNIQUE KEY `auditflow_id` (`auditflow_id`,`ptype`,`userid`,`flownum`),  KEY `create_id` (`create_id`),  KEY `companyid` (`companyid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程指定人'");

					ps.addBatch("update  t_order_detail td,t_order t  set td.billno=t.billno  where td.order_id=t.id and td.billno<>t.billno");

					ps.addBatch("update s_permission set functionname='操作导航'  where functionvalue='maindivdata'");
					ps.addBatch("update s_permission set fvalue='maindivdata2:echardiv',fseq=3,functionname='数据统计',functionvalue='maindivdata2' where id='C87A48DB35100001516515E72C40BA60'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA882FF546F00001212B1B2B9B971E5C', 3, 100, '系统管理', 'systemset', 27, '审批流程配置', 'auditflowdata', 1, 2, '保存', 'auditflowdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-29 17:03:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-29 17:04:45', 1),"
							+ "('CA8830030EE00001C8DADBF01560AB70', 3, 100, '系统管理', 'systemset', 27, '审批流程配置', 'auditflowdata', 1, 1, '查看', 'auditflowdata:setread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-29 17:04:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-11-29 17:04:27', 1),"
							+ " ('CA90DD1AE4200001A95FD6FB1E208150', 3, 2, '首页', 'mainmodel', 3, '数据统计', 'maindivdata2', 1, 3, '生产工序统计图', 'maindivdata2:stepdiv', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-12-26 16:00:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-12-26 16:02:29', 1),"
							+ "('CA90DD1B1220000119E21150E0D01F92', 3, 2, '首页', 'mainmodel', 3, '数据统计', 'maindivdata2', 1, 2, '订单生产统计图', 'maindivdata2:orderdiv', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-12-26 16:00:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-12-26 16:02:08', 1),"
							+ "('CA90DD1D769000016C71162EC60039C0', 3, 2, '首页', 'mainmodel', 3, '数据统计', 'maindivdata2', 1, 1, '统计报表', 'maindivdata2:report', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-12-26 16:01:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2023-12-26 16:01:34', 1)");

					ps.addBatch("update s_permission set seq=9  where id='CA0620CC67D00001138C5AFB184311DD'");

					ps.addBatch("ALTER TABLE `storeindetail` ADD COLUMN `invoicecount` DOUBLE NULL DEFAULT '0' AFTER `BKremark`, ADD COLUMN `invoicemoney` DOUBLE NULL DEFAULT '0' AFTER `invoicecount`");

					ps.addBatch("ALTER TABLE `storeoutdetail` ADD COLUMN `invoicecount` DOUBLE NULL DEFAULT '0' AFTER `BKremark`, ADD COLUMN `invoicemoney` DOUBLE NULL DEFAULT '0' AFTER `invoicecount`");

					ps.addBatch("ALTER TABLE `stageoutsourcingdetail` ADD COLUMN `invoicecount` DOUBLE NULL DEFAULT '0' AFTER `BKremark`, ADD COLUMN `invoicemoney` DOUBLE NULL DEFAULT '0' AFTER `invoicecount`");

					ps.addBatch("ALTER TABLE `outsourcingindetail` ADD COLUMN `invoicecount` DOUBLE NULL DEFAULT '0' AFTER `BKremark`, ADD COLUMN `invoicemoney` DOUBLE NULL DEFAULT '0' AFTER `invoicecount`");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `changeproperty` INT(1) NOT NULL DEFAULT '0' COMMENT '其他入库可更新商品的属性信息' AFTER `currencylist`,ADD COLUMN `batchnolist` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '其他入库批号列表' AFTER `changeproperty`,ADD COLUMN `otherincheck` INT(1) NOT NULL DEFAULT '0' COMMENT '其他入库库存大于0不能入库' AFTER `batchnolist`,ADD COLUMN `applyitemadd` INT(1) NULL DEFAULT '0' COMMENT '采购申请添加物料配置' AFTER `orderitemadd`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("update t_order_progress tp,staffinfo sf set tp.user_id=sf.staffid where  tp.user_id='null' and tp.create_id=sf.userid");

					ps.addBatch("ALTER TABLE `t_auditflow` ADD COLUMN `isback` INT(1) NOT NULL DEFAULT '0' COMMENT '回退至开具人' AFTER `update_time`");
					ps.addBatch("ALTER TABLE `t_billflow` ADD COLUMN `back_id` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '回退人ID' AFTER `receive_remark`, ADD COLUMN `back_by` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '回退人' AFTER `back_id`,ADD COLUMN `backflownum` INT(1) NOT NULL DEFAULT '0' COMMENT '回退流程号' AFTER `back_by`,ADD COLUMN `backflowname` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '回退流程名' AFTER `backflownum`");

					ps.addBatch("update t_billflow tb set tb.back_id = tb.receive_id,tb.back_by=tb.receive_by,tb.backflownum=tb.preflownum,tb.backflowname=tb.preflowname where tb.flowfstatus = 2 and tb.back_id=''");

					ps.addBatch("update t_progress tp,s_company_config sc set  tp.finishcount=round((tp.progress_count-(tp.freturn_count-tp.return_count)),sc.countbit),isfinish=if(work_count<=round(finishcount+invalid_count,sc.countbit),1,0) where tp.companyid=sc.company_id and (select 1 from r_mainreturn rm where rm.detail_id=tp.detail_id and rm.finishtype=2 limit 1)");

					ps.addBatch(" update t_order_detail td,s_company_config sc set td.t_finishcount=round(ifnull((select min((tp.progress_count-(tp.freturn_count-tp.return_count))/tp.progress_scale) from t_progress tp where tp.detail_id=td.id and if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0), "
							+ " sc.countbit ),td.t_invailcount=round(ifnull((select sum(tp.invalid_count/tp.progress_scale) from t_progress tp where tp.detail_id=td.id and if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0), "
							+ " sc.countbit ), td.t_isfinish=if(td.item_count>round(td.t_finishcount+td.t_invailcount,sc.countbit),0,1) where td.companyid=sc.company_id and (select 1 from r_mainreturn rm where rm.detail_id=td.id and rm.finishtype=2 limit 1)");
					ps.addBatch("update t_order tr ,s_company_config sc set tr.finishcount=round(ifnull((select min(if(td.t_finishcount/td.must_item_count>1,1,(td.t_finishcount/td.must_item_count))) from t_order_detail td where td.order_id=tr.id),0)*tr.max_order_count,"
							+ " sc.countbit ),tr.canincount=if(tr.finishcount>tr.incount,round(tr.finishcount-tr.incount,sc.countbit),0)  where tr.companyid=sc.company_id and (select 1 from r_mainreturn rm where rm.order_id=tr.id and rm.finishtype=2 limit 1)");

					ps.addBatch("ALTER TABLE `t_billflow` ADD COLUMN `end_id` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '终止人ID' AFTER `back_send_remark`, ADD COLUMN `end_by` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '终止人' AFTER `end_id`, ADD COLUMN `end_time` DATETIME NULL DEFAULT NULL COMMENT '终止时间' AFTER `end_by`, ADD COLUMN `end_remark` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '终止说明' AFTER `end_time`");

					ps.addBatch("update iteminfo set inprice=0 where inprice is null ");
					ps.addBatch("update iteminfo set outprice=0 where outprice is null ");
					ps.addBatch("ALTER TABLE `iteminfo` CHANGE COLUMN `inprice` `inprice` DOUBLE NULL DEFAULT '0' COMMENT '进货单价' AFTER `imgurl`, CHANGE COLUMN `outprice` `outprice` DOUBLE NULL DEFAULT '0' COMMENT '零售单价' AFTER `inprice`");
					ps.addBatch("update outsourcing o,s_company_config c set o.count=round(ifnull((select sum(od.count) from outsourcingdetail od where o.outsourcingid=od.outsourcingid and od.`stype`='221'),0),c.countbit),o.materiel=round(ifnull((select sum(od.count) from outsourcingdetail od where o.outsourcingid=od.outsourcingid and od.`stype`='222'),0),c.countbit) where o.companyid=c.company_id");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `salesoutset` INT(1) NOT NULL DEFAULT '0' COMMENT '销售出库在未审核状态下含库存不足商品记录可保存' AFTER `otherincheck`");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");
					ps.addBatch("ALTER TABLE `stock` ADD COLUMN `stockremark` VARCHAR(200) NULL DEFAULT '' COMMENT '最新单据入库备注' AFTER `prodreqback_money`");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_auditflowmain` ( `id` varchar(36) NOT NULL COMMENT '主鍵', `companyid` varchar(50) NOT NULL DEFAULT '' COMMENT '组织编号',   `auditflownum` int(11) NOT NULL DEFAULT '0' COMMENT '序号',  `auditflowtablename` varchar(30) NOT NULL DEFAULT '' COMMENT '单据表名', `auditflowbillname` varchar(30) NOT NULL DEFAULT '' COMMENT '单据名称', `auditflowcount` int(1) NOT NULL DEFAULT '0' COMMENT '审批流程数量', `update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '操作人ID',  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '操作人',  `update_time` datetime DEFAULT NULL COMMENT '操作日期',  PRIMARY KEY (`id`), UNIQUE KEY `companyid` (`companyid`,`auditflownum`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单据审批权限'");

					ps.addBatch("ALTER TABLE `t_auditflow` DROP INDEX `companyid`");
					ps.addBatch("ALTER TABLE `t_auditflow` ADD COLUMN `auditflowmainid` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '主审批ID' AFTER `companyid`,   ADD COLUMN `auditflowname` VARCHAR(50) NOT NULL DEFAULT '默认审批流程' COMMENT '流程名称' AFTER `auditflowbillname`, DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `companyid` (`companyid`)");
					ps.addBatch("update t_auditflow t set t.auditflowmainid=concat('a',t.id) where 1=1");

					ps.addBatch("ALTER TABLE `t_auditflow`  ADD UNIQUE INDEX `auditflowmainid` (`auditflowmainid`, `auditflownum`, `auditflowname`)");

					ps.addBatch("INSERT INTO `t_auditflowmain` (`id`, `companyid`, `auditflownum`, `auditflowtablename`, `auditflowbillname`, `auditflowcount`, `update_id`, `update_by`, `update_time`) select auditflowmainid, companyid, auditflownum, auditflowtablename, auditflowbillname, 1, update_id, update_by, update_time from t_auditflow");

					ps.addBatch("ALTER TABLE `t_billflow` ADD COLUMN `auditflowmain_id` VARCHAR(36) NOT NULL DEFAULT '' COMMENT '单据审核ID' AFTER `auditflow_id`, ADD INDEX `auditflowmain_id` (`auditflowmain_id`)");
					ps.addBatch("update t_billflow t set t.auditflowmain_id=concat('a',t.auditflow_id) where 1=1");

					ps.addBatch("ALTER TABLE `customer` ADD COLUMN `c_rate` DOUBLE NULL DEFAULT '0' COMMENT '税率(%)' AFTER `visitrole`");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CA9BBE5FE65000011A8215141BA015A8', 3, 50, '基础模块', 'basicset', 60, '往来单位管理', 'customerdata', 1, 27, '批量变更税率', 'customerdata:c_rate', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-01-29 11:16:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-01-29 11:19:27', 1)");

					ps.addBatch("update outsourcingindetail s set s.salesorderdetailid='',s.salesorderid='',s.salesorderorderid='' where s.salesorderdetailid='null'");

					ps.addBatch("update customeryear c,s_company_config s set c.rec_add_money=round(c.rec_sellout_money-c.rec_sellin_money,s.moneybit) where c.companyid=s.company_id and c.rec_add_money<>round(c.rec_sellout_money-c.rec_sellin_money,s.moneybit)");

					ps.addBatch("update customeryear c,s_company_config s set  c.receivable=round(c.rec_add_money-c.rec_money-c.rec_cmoney,s.moneybit) where c.companyid=s.company_id and c.receivable<>round(c.rec_add_money-c.rec_money-c.rec_cmoney,s.moneybit)");

					ps.addBatch("update customer cm,s_company_config sc set cm.receivable=round(ifnull((select sum(cb.receivable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.beginreceivable,sc.moneybit)   where cm.companyid=sc.company_id");

				}

				if (version < 2.21 && newversion >= 2.21) {
					// ps.addBatch("UPDATE  t_progress set start_time =(select min(if(begintime is null,create_date,begintime)) from t_order_progress where progress_id=t_progress.id and fstatus=1 ) where start_time is not null and start_time=finish_time");
					ps.addBatch("ALTER TABLE `iteminfo` ADD COLUMN `outsourcingprice` DOUBLE NULL DEFAULT '0' COMMENT '委外加工单价' AFTER `outprice`");
					ps.addBatch("update s_permission set fname='BOM详情数据导出'  where fvalue='iteminfodata:exportbomdetail'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CAAB8D8CDA40000175711BE0469A1035', 3, 1, 'App端', 'appdata', 8, '商品查询', 'itemsearch', 1, 2, '查看委外加工单价', 'itemsearch:outsourcingprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:06:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:07:38', 1),"
							+ "('CAAB8D9277D00001CF701C009AF012A6', 3, 40, '报表模块', 'reportmodel', 20, '库存查询', 'stockdata', 1, 4, '查看委外加工单价', 'stockdata:outsourcingprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:06:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:07:40', 1),"
							+ "('CAAB8D94199000014B13B19012D81D98', 3, 40, '报表模块', 'reportmodel', 30, '库存状况', 'stockstatedata', 1, 3, '查看委外加工单价', 'stockstatedataoutsourcingprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:06:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:07:44', 1),"
							+ "('CAAB8D96CAC000017BBB14F715E01638', 3, 50, '基础模块', 'basicset', 10, '商品管理', 'iteminfodata', 1, 17, '查看委外加工单价', 'iteminfodata:outsourcingprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:07:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-03-18 14:07:48', 1),"
							+ " ('CAB7660B8EE000019DD882901AF915A2', 3, 25, '生产管理', 'orderset', 20, '派工管理', 'staffjobdata', 1, 3, '删除派工', 'staffjobdata:candelete', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-04-24 09:23:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-04-24 09:23:42', 1)");

					ps.addBatch("update storeout s,customer c set s.linkman=c.customerlinkname,s.linkphone=c.customerphone,s.deliveryadrr=c.customeraddress  where s.customerid=c.customerid and s.deliveryadrr='' and s.linkman='' and c.customeraddress<>s.deliveryadrr");

					ps.addBatch("update processinout s,customer c set s.linkman=c.customerlinkname,s.linkphone=c.customerphone,s.deliveryadrr=c.customeraddress  where s.customerid=c.customerid and s.deliveryadrr='' and s.linkman='' and c.customeraddress<>s.deliveryadrr");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `caltype` INT(1) NULL DEFAULT '0' COMMENT '销售计价辅助数量类型' AFTER `uploadcomfilesize`,ADD COLUMN `bomcountbit` INT(11) NULL DEFAULT '5' COMMENT 'BOM用量辅助公式计算结果的小数点位数' AFTER `countbit`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `itemsplits` ADD COLUMN `assistformula` VARCHAR(100) NULL DEFAULT '' COMMENT '用量辅助计算公式' AFTER `remark`");

					ps.addBatch("ALTER TABLE `prodrequisition_work_total` ADD COLUMN `assistformula` VARCHAR(100) NULL DEFAULT '' COMMENT '用量辅助计算公式' AFTER `unitcount`");

					ps.addBatch("update t_order_detail td,t_order t set td.batchno=t.batchno where td.order_id=t.id and length(t.batchno)>0 and td.batchno=''");

					ps.addBatch("update accountyear s,s_company_config sc set s.in_money=round(ifnull((select sum(am.in_money) from accountmonth am where am.accountid=s.accountid and am.syear=s.syear group by am.syear),0),sc.moneybit),s.out_money=round(ifnull((select sum(am.out_money) from accountmonth am where am.accountid=s.accountid and am.syear=s.syear group by am.syear),0),sc.moneybit)  where s.companyid=sc.company_id");
					ps.addBatch("update account s,s_company_config sc set s.money=round(s.beginmoney+ifnull((select sum(am.money) from accountmonth am where am.accountid=s.accountid),0),sc.moneybit) where s.companyid=sc.company_id");

					ps.addBatch("update outsourcingdetail set  billstatus ='1' where   status='1' and count<=incount and billstatus='0'");
					ps.addBatch("update outsourcing o,s_company_config c set o.incount=round(ifnull((select sum(if(od.stype='251',od.count,-od.count)) from outsourcingindetail od where o.outsourcingid=od.relationmainid and od.`status`=1),0),c.countbit) where  o.incount<>round(ifnull((select sum(if(od.stype='251',od.count,-od.count)) from outsourcingindetail od where o.outsourcingid=od.relationmainid and od.`status`=1),0),c.countbit) and o.`status`=1 and o.companyid=c.company_id");
					ps.addBatch("update outsourcing set billstatus = '1' where   billstatus='0' and status='1' and (select count(*) from outsourcingdetail where outsourcingid=outsourcing.outsourcingid and status='1' and billstatus='0' and stype='221')=0 ");

					// update t_progress ts,s_company_config sc set
					// ts.progress_count=round(ifnull((select
					// sum(tps.detail_count) from t_order_progress tps where
					// tps.progress_id=ts.id and
					// tps.fstatus=1),0),sc.countbit),ts.return_count=round(ifnull((select
					// sum(tps.return_count) from t_order_progress tps where
					// tps.progress_id=ts.id and
					// tps.fstatus=1),0),sc.countbit),ts.invalid_count=round(ifnull((select
					// sum(tps.invalid_count) from t_order_progress tps where
					// tps.progress_id=ts.id and tps.fstatus=1),0),sc.countbit)
					// where ts.companyid=sc.company_id and (select 1 from
					// t_order_progress top where top.progress_id=ts.id and
					// top.fstatus=2 limit 1);
					// update t_progress tp ,s_company_config sc set
					// tp.finishcount=round(tp.progress_count-(tp.freturn_count-tp.return_count),sc.countbit),tp.isfinish=if(tp.work_count>round(tp.progress_count-(tp.freturn_count-tp.return_count)+tp.invalid_count,sc.countbit),0,1)
					// where tp.companyid=sc.company_id and (select 1 from
					// t_order_progress top where top.progress_id=ts.id and
					// top.fstatus=2 limit 1);

				}

				if (version < 2.3 && newversion >= 2.3) {

					ps.addBatch("ALTER TABLE `t_workshop` ADD COLUMN `production` DOUBLE NULL DEFAULT '0' COMMENT '日产工时' AFTER `workshop_remark`,ADD COLUMN `productioncount` DOUBLE NULL DEFAULT '0' COMMENT '日产数量' AFTER `workshop_remark`");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_deviceclass` (  `classid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `classname` varchar(50) DEFAULT NULL COMMENT '分类名',   `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',"
							+ " `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `parentid` varchar(36) DEFAULT '' COMMENT '父级',  PRIMARY KEY (`classid`),  KEY `companyid` (`companyid`),  KEY `create_id` (`create_id`), KEY `parentid` (`parentid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					// ps.addBatch("CREATE TABLE IF NOT EXISTS `t_devicestatus` (  `dsid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',   `deviceid` varchar(36) DEFAULT '' COMMENT '设备ID',  `fstatus` int(1) DEFAULT '0' COMMENT '状态',  `oldfstatus` int(1) DEFAULT '0' COMMENT '原状态',  `remark` varchar(200) DEFAULT NULL COMMENT '变更说明',  `imgurl` varchar(400) DEFAULT '' COMMENT '文件列表',"
					// +
					// "  `filesize` int(11) DEFAULT '0' COMMENT '文件大小',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人', `create_date` datetime DEFAULT NULL COMMENT '创建时间',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建ID',   PRIMARY KEY (`dsid`),  KEY `companyid` (`companyid`),   KEY `deviceid` (`deviceid`),   KEY `create_date` (`create_date`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					//
					// ps.addBatch("CREATE TABLE IF NOT EXISTS `t_device_schedule` ( `id` varchar(36) NOT NULL COMMENT '主键', `fstatus` int(1) DEFAULT '0' COMMENT '状态', `progress_id` varchar(36) DEFAULT '' COMMENT '订单工序ID', `companyid` varchar(50) DEFAULT '' COMMENT '组织编号', `order_id` varchar(50) DEFAULT '' COMMENT '订单ID',"
					// +
					// "`detail_id` varchar(50) DEFAULT '' COMMENT '明细编号', `step_id` varchar(50) DEFAULT '' COMMENT '工序编号',  `step_name` varchar(50) DEFAULT '' COMMENT '工序名称',  `sno` int(11) DEFAULT '1' COMMENT '顺序号',  `s_count` double DEFAULT '0' COMMENT '排产数量',  `finishcount` double DEFAULT '0' COMMENT '完成数量',  `c_count` double DEFAULT '0' COMMENT '转产数量',  `device_id` varchar(36) DEFAULT '' COMMENT '排产设备',"
					// +
					// "`c_device_id` varchar(36) DEFAULT '' COMMENT '转设备',  `remark` varchar(200) DEFAULT '' COMMENT '排产说明', `c_remark` varchar(200) DEFAULT '' COMMENT '转设备说明',  `c_update_id` varchar(36) DEFAULT '' COMMENT '转设备操作人ID',  `c_update_by` varchar(50) DEFAULT '' COMMENT '转设备操作人', `c_update_date` datetime DEFAULT NULL COMMENT '转设备操作日期',  `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_date` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) DEFAULT '' COMMENT '更新人',  `update_date` datetime DEFAULT NULL COMMENT '更新日期',"
					// +
					// " PRIMARY KEY (`id`),  KEY `order_id` (`order_id`),   KEY `companyid` (`companyid`),   KEY `detail_id` (`detail_id`),  KEY `progress_id` (`progress_id`),   KEY `device_id` (`device_id`),   KEY `step_id` (`step_id`),  KEY `sno` (`sno`),  KEY `fstatus` (`fstatus`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备排产表'");
					//
					// ps.addBatch("ALTER TABLE `t_progress` ADD COLUMN `s_device_count` DOUBLE NULL DEFAULT '0' COMMENT '设备已排数量' AFTER `work_count`,ADD COLUMN `scheduledate` DATETIME NULL DEFAULT NULL COMMENT '最新排产时间' AFTER `s_device_count`");

					ps.addBatch("ALTER TABLE `t_device` "
							+ "CHANGE COLUMN `companyid` `companyid` VARCHAR(50) NULL DEFAULT '' COMMENT '组织编号' AFTER `fstatus`,"
							+ "CHANGE COLUMN `device_id` `device_id` VARCHAR(50) NULL DEFAULT '' COMMENT '设备编号' AFTER `companyid`,"
							+ "CHANGE COLUMN `device_name` `device_name` VARCHAR(50) NULL DEFAULT '' COMMENT '设备名称' AFTER `device_id`,"
							+ "CHANGE COLUMN `device_remark` `device_remark` VARCHAR(200) NULL DEFAULT '' COMMENT '设备备注' AFTER `device_name`,"
							+ "ADD COLUMN `devicefstatus` INT(1) NULL DEFAULT '0' COMMENT '排产设备状态' AFTER `device_remark`,"
							+ "ADD COLUMN `remark1` VARCHAR(200) NULL DEFAULT '' COMMENT '设备状态说明' AFTER `devicefstatus`,"
							+ "ADD COLUMN `remark2` VARCHAR(200) NULL DEFAULT '' COMMENT '看板设备备注' AFTER `remark1`,"
							+ "ADD COLUMN `production` DOUBLE NULL DEFAULT '0' COMMENT '日产量' AFTER `remark2`,ADD COLUMN `classid` VARCHAR(36) NULL DEFAULT '' COMMENT '分类ID' AFTER `production`,ADD INDEX `classid` (`classid`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_order_progresstime` ( `id` varchar(36) NOT NULL COMMENT '主键', "
							+ "`companyid` varchar(36) DEFAULT NULL COMMENT '组织编号', `fstatus` int(1) DEFAULT NULL COMMENT '状态', `user_id` varchar(36) DEFAULT NULL COMMENT '生产员工',   `begintime` datetime DEFAULT NULL COMMENT '开始时间',  `endtime` datetime DEFAULT NULL COMMENT '结束时间', `remark` varchar(100) DEFAULT '' COMMENT '添加原因', `invalidremark` varchar(100) DEFAULT '' COMMENT '作废原因',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',  `create_date` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID', `update_by` varchar(36) DEFAULT NULL COMMENT '更新人', `update_date` datetime DEFAULT NULL COMMENT '更新日期',   PRIMARY KEY (`id`),  KEY `user_id` (`user_id`) ,  KEY `companyid` (`companyid`),  KEY `create_date` (`create_date`),  KEY `begintime` (`begintime`),  KEY `endtime` (`endtime`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='不计件时间表'");

					ps.addBatch("ALTER TABLE `t_order_progress` ADD COLUMN `stype` INT(1) NULL DEFAULT '0' COMMENT '不计件' AFTER `step_no`");

					ps.addBatch("ALTER TABLE `prodstoragedetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `storemovedetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `otherinoutdetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `outsourcingindetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `processinoutdetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `prodrequisitiondetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `reportlossdetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `splitsdetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `storecheckdetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");
					ps.addBatch("ALTER TABLE `storeoutdetail` CHANGE COLUMN `batchno` `batchno` VARCHAR(50) NULL DEFAULT '' COMMENT '批号' ");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CAC4ADEF7EC000015BAA9700576E1A60', 3, 26, '生产报表', 'orderreportset', 90, '车间产能统计', 'workshopstatistics', 1, 1, '查看', 'workshopstatistics:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-04 15:40:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-04 15:41:32', 1),"
							+ "('CAC4ADFE70B00001932E16604900E390', 3, 26, '生产报表', 'orderreportset', 90, '车间产能统计', 'workshopstatistics', 1, 2, '导出数据', 'workshopstatistics:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-04 15:41:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-04 15:42:02', 1),"
							+ "('CAC5A1FB3E7000015B1B1C40181CF8B0', 3, 25, '生产管理', 'orderset', 55, '不计件时段配置', 'nonpiecesetdata', 1, 1, '查看', 'nonpiecesetdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:45:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:46:16', 1),"
							+ "('CAC5A2070BB000018A521622ECAD1340', 3, 25, '生产管理', 'orderset', 55, '不计件时段配置', 'nonpiecesetdata', 1, 2, '新增', 'nonpiecesetdata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:46:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:46:45', 1),"
							+ "('CAC5A20C89A000012559190517F02D70', 3, 25, '生产管理', 'orderset', 55, '不计件时段配置', 'nonpiecesetdata', 1, 3, '作废', 'nonpiecesetdata:invaild', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:46:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:47:01', 1),"
							+ "('CAC5A2105FF000016147F90019C012B8', 3, 25, '生产管理', 'orderset', 55, '不计件时段配置', 'nonpiecesetdata', 1, 4, '导出数据', 'nonpiecesetdata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:47:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-07 14:47:17', 1),"
							+ "('CAC8C975D8900001687759CF1D42106C', 3, 50, '基础模块', 'basicset', 93, '设备信息', 'devicedata', 1, 10, '批量变更分类', 'devicedata:changeclass', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:57:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:59:01', 1),"
							+ "('CAC8C97624B00001F016365A13A01451', 3, 50, '基础模块', 'basicset', 93, '设备信息', 'devicedata', 1, 8, '修改分类', 'devicedata:editclass', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:57:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:58:19', 1),"
							+ "('CAC8C97652600001127D153011E07A90', 3, 50, '基础模块', 'basicset', 93, '设备信息', 'devicedata', 1, 7, '新增分类', 'devicedata:newclass', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:57:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:57:59', 1),"
							+ "('CAC8C98A8DB00001545312521FC06A10', 3, 50, '基础模块', 'basicset', 93, '设备信息', 'devicedata', 1, 9, '删除分类', 'devicedata:delclass', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:58:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 09:58:43', 1),"
							+ "('CACCBEC5A2600001CE7798409058A500', 3, 3, '看板管理', 'boardset', 70, '车间产能看板', 'boardworkshoptj', 1, 1, '查看', 'boardworkshoptj:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-29 17:06:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-29 17:06:49', 1)");
					// +
					// "('CAC8C9A1763000019B92E0DFBBBB1F32', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 1, '查看', 'scheduledata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:00:17', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:00:52', 1),"
					// +
					// "('CAC8C9ABD4C0000136FFC56B1C68197C', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 2, '排产', 'scheduledata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:00:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:01:20', 1),"
					// +
					// "('CAC8C9B1B2B00001D1F71DE588C448F0', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 3, '修改排产记录', 'scheduledata:modify', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:01:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:01:56', 1),"
					// +
					// "('CAC8C9BABDD00001AADD117B9A901000', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 6, '导出数据', 'scheduledata:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:02:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:04:20', 1),"
					// +
					// "('CAC8C9C3774000016217112012A716BD', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 4, '查看设备状态列表', 'scheduledata:showfstatus', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:02:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:04:17', 1),"
					// +
					// "('CAC8C9D5E600000191841F3013307160', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 5, '批量变更设备状态', 'scheduledata:change', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:03:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:04:10', 1),"

					// +
					// "('CAC8CA1793700001F346DDF01BFBE4A0', 3, 3, '看板管理', 'boardset', 90, '设备产能看板', 'boarddevice', 1, 1, '查看', 'boarddevicej:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:08:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:11:05', 1),"
					// +
					// "('CAC8CA17D1100001DBFA39CB23601045', 3, 3, '看板管理', 'boardset', 80, '设备排产看板', 'boardschedule', 1, 1, '查看', 'boardschedule:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:08:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:10:22', 1)");

					ps.addBatch("ALTER TABLE `stock` ADD INDEX `count` (`count`)");
					ps.addBatch("ALTER TABLE `stock` DROP COLUMN `totalcount`, DROP COLUMN `totalmoney`, DROP COLUMN `begincount`, DROP COLUMN `begintotal`, DROP COLUMN `t_totalcount`, DROP COLUMN `t_totalmoney`, DROP COLUMN `sellcount`, DROP COLUMN `sellmoney`, DROP COLUMN `sellcost`, DROP COLUMN `t_sellcount`, DROP COLUMN `t_sellmoney`, DROP COLUMN `t_sellcost`, DROP COLUMN `profitcount`,"
							+ "DROP COLUMN `profitmoney`, DROP COLUMN `losscount`, DROP COLUMN `lossmoney`, DROP COLUMN `in_count`, DROP COLUMN `in_money`, DROP COLUMN `out_count`, DROP COLUMN `out_money`, DROP COLUMN `splitsin_count`,"
							+ "DROP COLUMN `splitsin_money`, DROP COLUMN `splitsout_count`, DROP COLUMN `splitsout_money`, DROP COLUMN `prodreq_count`, DROP COLUMN `prodreq_money`, DROP COLUMN `prodstorage_count`, DROP COLUMN `prodstorage_money`, DROP COLUMN `otherin_count`, DROP COLUMN `otherin_money`, DROP COLUMN `otherout_count`, DROP COLUMN `otherout_money`, DROP COLUMN `processout_count`, DROP COLUMN `processout_money`, DROP COLUMN `processin_count`, DROP COLUMN `processin_money`, DROP COLUMN `outsourcing_count`, DROP COLUMN `outsourcing_cost`, DROP COLUMN `outsourcing_money`, DROP COLUMN `prodreqback_count`, DROP COLUMN `prodreqback_money`");

					ps.addBatch("delete from stock where   count=0 and money=0");

					ps.addBatch("ALTER TABLE `stock`  DROP INDEX `companyid`, ADD UNIQUE INDEX `companyid` (`itemid`, `houseid`, `batchno`)");

					ps.addBatch("ALTER TABLE `itemmonth` DROP INDEX `com_item_house`, ADD UNIQUE INDEX `com_item_house` (`itemid`, `houseid`, `batchno`, `sdate`)");

					// select * from t_progress tp where tp.step_name='';
					// ps.addBatch("update t_progress tp,t_stepnew tw set tp.step_name=tw.stepnewname where tp.step_name='' and tp.stepnewid=tw.stepnewid");

					ps.addBatch("update prodrequisition_work_total pw,t_order tor set pw.scheduleid=tor.scheduleid where pw.scheduleid='' and pw.worksheetid=tor.id");
					ps.addBatch("update prodrequisition_work_total pw,t_order tor set pw.worksheetitemid=tor.itemid where pw.worksheetitemid='null' and pw.worksheetid=tor.id");

					ps.addBatch("ALTER TABLE `t_order_detail` ADD COLUMN `finishdate` DATETIME NULL DEFAULT NULL COMMENT '生产完成日期' AFTER `t_isfinish`, ADD INDEX `finishdate` (`finishdate`)");
					ps.addBatch("update t_order_detail td set td.finishdate=(select max(tp.finish_time) from t_progress tp where tp.detail_id=td.id) where td.t_isfinish=1");

					ps.addBatch("ALTER TABLE `iteminfo`  ADD INDEX `customerid` (`customerid`)");

					// ps.addBatch("ALTER TABLE `customer` ADD INDEX `typeid` (`typeid`)");
					// ps.addBatch("ALTER TABLE `iteminfo` ADD INDEX `classid` (`classid`), ADD INDEX `customerid` (`customerid`)");

					ps.addBatch("ALTER TABLE `t_order_progress` ADD COLUMN `detail_nos` VARCHAR(2000) NULL DEFAULT '' COMMENT '完工细码号' AFTER `stype`, ADD COLUMN `invalid_nos` VARCHAR(1000) NULL DEFAULT '' COMMENT '报废细码号' AFTER `detail_nos`");
					ps.addBatch("ALTER TABLE `t_detail_progress` ADD INDEX `fstatus` (`fstatus`), ADD INDEX `detail_no` (`detail_no`)");

					// ps.addBatch("ALTER TABLE `iteminfo` CHANGE COLUMN `mcode` `mcode` VARCHAR(100) NULL DEFAULT NULL COMMENT '助记码' AFTER `sformat`");

					ps.addBatch("update t_order_progress tp set tp.detail_nos=ifnull((select group_concat(p.detail_no) from t_detail_progress p where p.order_progressid=tp.id and (p.invalid_count=0 or (p.invalid_count=1 and p.freturn_count>=p.invalid_count))),''),tp.invalid_nos=ifnull((select group_concat(p.detail_no) from t_detail_progress p where p.order_progressid=tp.id and  p.invalid_count=1  and p.freturn_count=0),'') where tp.progress_type=1 and (tp.detail_count>0 or (tp.invalid_count>0 and tp.return_invalid=0))");
					// select * from t_order_progress tp where
					// tp.progress_type=1 and (tp.return_count>0 or
					// tp.return_invalid>0) order by progress_id, recordtime;
					ps.addBatch("update t_order_progress tp,t_progress t set tp.step_no=t.step_no where tp.progress_id=t.id and tp.progress_type=1 and (tp.return_count>0 or   tp.return_invalid>0)");
					// select detail_no from t_detail_progress where
					// progress_id='8a6fea4e1f054c4eb10ed04c22beb196' and
					// freturn_count>0;
					ps.addBatch("update customer s set s.staff='' where s.staff='null'");
					ps.addBatch(" update storeout s,staffinfo st set s.operate_by=st.staffid where s.operate_by='null' and s.create_id=st.userid");
					ps.addBatch(" update storeoutdetail s,staffinfo st set s.operate_by=st.staffid where s.operate_by='null' and s.create_id=st.userid");

					ps.addBatch(" update salesorder s,staffinfo st set s.operate_by=st.staffid where s.operate_by='null' and s.create_id=st.userid");

					ps.addBatch(" update salesorderdetail s,staffinfo st set s.operate_by=st.staffid where s.operate_by='null' and s.create_id=st.userid");

					// update storeout s,staffinfo st set
					// s.operate_by=st.staffid where s.operate_by='null' and
					// s.create_id=st.userid;
					// update storeoutdetail s,staffinfo st set
					// s.operate_by=st.staffid where s.operate_by='null' and
					// s.create_id=st.userid;
					//
					// update salesorder s,staffinfo st set
					// s.operate_by=st.staffid where s.operate_by='null' and
					// s.create_id=st.userid;
					// update salesorderdetail s,staffinfo st set
					// s.operate_by=st.staffid where s.operate_by='null' and
					// s.create_id=st.userid;

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `enteritemadd` INT(1) NULL DEFAULT '0' COMMENT '采购入库可新增商品配置' AFTER `applyitemadd`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `customer` ADD INDEX `role` (`role`)");

					ps.addBatch("ALTER TABLE `prodrequisition` CHANGE COLUMN `worksheetid` `worksheetid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联工单id' AFTER `reworksheet`");

					ps.addBatch("update prodrequisition  p set p.worksheetid='' where p.worksheetid is null");
					ps.addBatch("update prodrequisitiondetail  p set p.worksheetid='' where p.worksheetid is null");

					ps.addBatch("ALTER TABLE `t_detail_code` ADD COLUMN `sid1` VARCHAR(50) NULL DEFAULT '' COMMENT '配对ID1' AFTER `printing`,ADD COLUMN `sid2` VARCHAR(50) NULL DEFAULT '' COMMENT '配对ID2' AFTER `sid1`,ADD COLUMN `sid3` VARCHAR(50) NULL DEFAULT '' COMMENT '配对ID3' AFTER `sid2`, ADD INDEX `sid1` (`sid1`), ADD INDEX `sid2` (`sid2`), ADD INDEX `sid3` (`sid3`)");

					ps.addBatch("update prodrequisition_work_total s,t_order t set s.worksheetbillno=t.billno where s.worksheetid=t.id and s.worksheetbillno<>t.billno");

					ps.addBatch("update customer c set c.selltype='0' where c.selltype=''");
					ps.addBatch("update salesorder c set c.selltype='0' where c.selltype=''");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CAD46F8041100001435DAE96EA0D15A1', 3, 1, 'App端', 'appdata', 3, '细码扫描', 'detailscan', 1, 3, '修改细码配对ID', 'detailscan:modify', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-07-23 14:32:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-07-23 14:33:51', 1),"
							+ "('CAD46FAAB09000017457D2A0A3D715FE', 3, 25, '生产管理', 'orderset', 10, '工单管理', 'orderdata', 1, 18, '修改细码配对ID', 'orderdata:modifycodeid', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-07-23 14:35:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-07-23 14:35:53', 1)");

					ps.addBatch("create or replace view itemsplits_view as  SELECT im.inprice,im.barcode,im.unit,im.codeid,im.imgurl,im.itemname,im.sformat,im.classid,ifnull(ic.classname,'') as classname ,im.status,im.property1,im.property2,im.property3,im.property4,im.property5,im.class_id,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ims.* from itemsplits ims,iteminfo im left join itemclass ic on im.classid=ic.classid where ims.itemid=im.itemid order by ims.combitemid asc,ims.number asc");

					ps.addBatch("update prodrequisition_work_total set assistformula=''   where assistformula='null'");

				}

				if (version < 2.4 && newversion >= 2.4) {
					ps.addBatch("ALTER TABLE `t_detail_code` DROP INDEX `companyid_id`");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `exportdataset` (   `id` varchar(36) NOT NULL COMMENT '编号',   `bill_type` varchar(5) NOT NULL DEFAULT '' COMMENT '单类型',   `stype` int(1) NOT NULL DEFAULT '1' COMMENT '1-导出 2-打印', "
							+ " `colid` varchar(20) NOT NULL DEFAULT '' COMMENT '系统列名',  `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '公司id',  `sno` int(11) NOT NULL DEFAULT '1' COMMENT '顺序号',  `showtype` int(1) NOT NULL DEFAULT '1' COMMENT '1-只显示中文 2-只显示英文 3-显示中文与英文',  `tablename` varchar(20) NOT NULL DEFAULT '',"
							+ " `tableshowname` varchar(20) NOT NULL DEFAULT '',  `colname` varchar(50) NOT NULL DEFAULT '' COMMENT '原列名',  `chinesecolname` varchar(50) NOT NULL DEFAULT '' COMMENT '中文列名',   `engilishcolname` varchar(100) NOT NULL DEFAULT '' COMMENT '英文列名',  `maintitle` varchar(500) NOT NULL DEFAULT '' COMMENT '备注说明',  `emaintitle` varchar(1000) NOT NULL DEFAULT '' COMMENT '英文说明', "
							+ "`swidth` int(11) NOT NULL DEFAULT '10' COMMENT '列宽',  `sheight` int(11) NOT NULL DEFAULT '40' COMMENT '行高',  `create_id` varchar(36) NOT NULL DEFAULT '',  `isshow` int(1) NOT NULL DEFAULT '0' COMMENT '是否显示',   `create_by` varchar(50) NOT NULL DEFAULT '',"
							+ " `create_date` datetime DEFAULT NULL,   PRIMARY KEY (`id`),   UNIQUE KEY `uniqued` (`bill_type`,`stype`,`companyid`,`colid`),   KEY `sno` (`sno`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `quotation` (`quotationid` varchar(36) NOT NULL COMMENT '编号', `bill_type` varchar(5) DEFAULT NULL COMMENT '单据类型',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `orderid` varchar(36) DEFAULT NULL COMMENT '报价单号',  `operate_time` date DEFAULT NULL COMMENT '报价日期',  `plandate` date DEFAULT NULL COMMENT '报价有效期',   `selltype` varchar(2) DEFAULT NULL COMMENT '售价级别',   `operate_by` varchar(50) DEFAULT NULL COMMENT '业务员',    `customerid` varchar(36) DEFAULT NULL COMMENT '客户',  `customername` varchar(200) DEFAULT NULL COMMENT '客户名称',  `currency` varchar(50) DEFAULT '人民币' COMMENT '币种',  `count` double DEFAULT '0' COMMENT '总数量',"
							+ "`total` double DEFAULT '0' COMMENT '总金额',  `totaltax` double DEFAULT '0' COMMENT '总税额',  `totalmoney` double DEFAULT '0' COMMENT '价税总额',   `remark` varchar(200) DEFAULT '' COMMENT '备注',  `status` varchar(1) DEFAULT '0' COMMENT '状态',  `qstatus` int(1) DEFAULT 0 COMMENT '报价状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',"
							+ "`outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',   `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',"
							+ "`audit_time` datetime DEFAULT NULL COMMENT '审核时间',   PRIMARY KEY (`quotationid`),   KEY `bill_type` (`bill_type`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),    KEY `customerid` (`customerid`),   KEY `create_id` (`create_id`),   KEY `status` (`status`),   KEY `qstatus` (`qstatus`),   KEY `currency` (`currency`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `quotationdetail` ( `detailid` varchar(36) NOT NULL COMMENT '编号', `quotationid` varchar(36) DEFAULT NULL COMMENT '销售订单ID',  `goods_number` int(11) DEFAULT NULL COMMENT '序号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号', `orderid` varchar(36) DEFAULT NULL COMMENT '订单编号',  `operate_time` date DEFAULT NULL COMMENT '报价日期', `plandate` date DEFAULT NULL COMMENT '报价有效期',"
							+ "`operate_by` varchar(50) DEFAULT NULL COMMENT '业务员', `itemid` varchar(36) DEFAULT NULL COMMENT '商品编号',  `customerid` varchar(36) DEFAULT NULL COMMENT '客户',  `customername` varchar(200) DEFAULT NULL COMMENT '客户名称', `ordercount` double DEFAULT '0' COMMENT '订单数量', `price` double DEFAULT '0' COMMENT '单价',  `count` double DEFAULT '0' COMMENT '数量', `total` double DEFAULT '0' COMMENT '金额',  `taxrate` double DEFAULT '0' COMMENT '税率(%)',  `tax` double DEFAULT '0' COMMENT '税额', `taxprice` double DEFAULT '0' COMMENT '含税单价', `taxmoney` double DEFAULT '0' COMMENT '价税合计', `stype` varchar(5) DEFAULT NULL COMMENT '类型', `remark` varchar(200) DEFAULT '' COMMENT '备注',  `remark1` varchar(200) DEFAULT '' COMMENT '备注1',  `remark2` varchar(200) DEFAULT '' COMMENT '备注2',"
							+ " `remark3` varchar(200) DEFAULT '' COMMENT '备注3',  `status` varchar(1) DEFAULT '0' COMMENT '状态',   `qstatus` int(1) DEFAULT 0 COMMENT '报价状态',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT NULL COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT NULL COMMENT '审核人',"
							+ "`audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`detailid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),   KEY `customerid` (`customerid`),  KEY `itemid` (`itemid`),   KEY `create_id` (`create_id`),  KEY `quotationid` (`quotationid`,`status`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("ALTER TABLE `salesorderdetail` ADD COLUMN `relationdetailid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联报价明细id' AFTER `outsourcingin`, ADD COLUMN `relationorderid` VARCHAR(50) NULL DEFAULT '' COMMENT '关联报价单' AFTER `relationdetailid`, ADD COLUMN `relationmainid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联报价单主表id' AFTER `relationorderid`, ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`)");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES"
							+ "('CADFB2ABD9200001697C29001B1612B8', 3, 26, '生产报表', 'orderreportset', 38, '工单细码进度表', 'summaryprintitemdata', 1, 1, '查看', 'summaryprintitemdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-27 14:19:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-27 14:19:37', 1),"
							+ "('CADFB2B0DB20000142741D40E0B01296', 3, 26, '生产报表', 'orderreportset', 38, '工单细码进度表', 'summaryprintitemdata', 1, 2, '导出数据看', 'summaryprintitemdata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-27 14:19:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-27 14:20:03', 1),"
							// +
							// "('CADFF70ED27000016860CEF01139BB70', 3, 26, '生产报表', 'orderreportset', 65, '工序质检项报表', 'stepqualitydata', 1, 1, '查看', 'stepqualitydata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-28 10:14:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-28 10:14:55', 1),"
							// +
							// "('CADFF7165E600001CC7913BAE9C0145B', 3, 26, '生产报表', 'orderreportset', 65, '工序质检项报表', 'stepqualitydata', 1, 2, '导出数据', 'stepqualitydata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-28 10:14:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-28 10:15:14', 1),"
							+ "('CAE0498A08100001CDEED54F15101E42', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 1, '查看', 'quotationdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:15:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:41', 2),"
							+ "('CAE049920780000171A61330BE408520', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 2, '新增', 'quotationdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:16:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:44', 1),"
							+ "('CAE04995BEA00001A79C273028A02EF0', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 3, '修改', 'quotationdata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:16:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:46', 1),"
							+ "('CAE0499D98600001C745188EAF811743', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 4, '删除', 'quotationdata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:17:14', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:47', 1),"
							+ "('CAE049A208900001BA6CBBD01B60D3A0', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 5, '详情', 'quotationdata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:17:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:52', 1),"
							+ "('CAE049A7C4000001BD1A21B41B287000', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 6, '审核', 'quotationdata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:17:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:53', 1),"
							+ "('CAE049B048800001562218EFA580C8D0', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 7, '复制', 'quotationdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:18:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:21:58', 1),"
							+ "('CAE049B8CB10000170F31E00EDC61FA0', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 8, '作废', 'quotationdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:19:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:22:00', 1),"
							+ "('CAE049C10FF0000128DDED7D13A01594', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 9, '导出汇总', 'quotationdata:exporttotal', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:19:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:22:02', 1),"
							+ "('CAE049C92F5000015CDF56CE1C401DB6', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 10, '导出明细', 'quotationdata:exportdetail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:20:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:24:27', 1),"
							+ "('CAE049ED1F6000016119B4D01B90DF50', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 11, '打印配置', 'quotationdata:printset', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:22:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:24:32', 1),"
							+ "('CAE049F4F2D000017D471E001D3EB7E0', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 12, '打印', 'quotationdata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:23:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:24:38', 1),"
							+ "('CAE04A0DD650000144376BE019901B5F', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 13, '查看单价', 'quotationdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:24:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:25:28', 1),"
							+ "('CAE04A2796900001D6551244D0701604', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 15, '主附件查看', 'quotationdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:26:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:29:22', 1),"
							+ "('CAE04A2C3E3000015B3711B0C9F41187', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 14, '详情可修改信息', 'quotationdata:modifyinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:26:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:29:06', 1),"
							+ "('CAE04A48B1100001BB8E6980137C1AC5', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 16, '主附件上传', 'quotationdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:28:55', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:29:24', 1),"
							+ "('CAE04A550E2000012C49115969211735', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 17, '查看商品BOM', 'quotationdata:showbow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:29:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-08-29 10:30:07', 1),"
							+ "('CAE1F55E69A00001E4AD15CBAD1BC370', 3, 20, '销售模块', 'storeoutmodel', 20, '送货管理', 'deliverdata', 1, 22, '主附件查看', 'deliverdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-03 14:52:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-03 14:54:46', 1),"
							+ "('CAE1F5685C2000012AB41B7081FC1CEA', 3, 20, '销售模块', 'storeoutmodel', 20, '送货管理', 'deliverdata', 1, 21, '主附件上传', 'deliverdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-03 14:53:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-03 14:54:48', 1),"
							+ "('CAE1FDAE1F10000112E211A013D07F00', 3, 20, '销售模块', 'storeoutmodel', 4, '报价管理', 'quotationdata', 1, 8, '修改报价状态', 'quotationdata:changestatus', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-03 17:18:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-03 17:18:23', 1),"
							+ "('CAE479E12C500001C1BD539016A06AC0', 3, 20, '销售模块', 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 27, '查看商品BOM', 'salesorderdata:showbow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-11 10:36:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-11 10:36:41', 1),"
							+ "('CAE48517B160000134B018C0430E92B0', 3, 35, '财务模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 27, '上传附件', 'inoutmanage:mainfileupload', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-11 13:52:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-11 14:00:37', 1),"
							+ "('CAE48590AB3000018C144DF016202950', 3, 35, '财务模块', 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 28, '查看附件', 'inoutmanage:mainfileread', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-11 14:00:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-09-11 14:00:54', 1)");

					// INSERT INTO `s_permission` (`id`, `ptype`, `pseq`,
					// `parentname`, `parentvalue`, `fseq`, `functionname`,
					// `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`,
					// `description`, `create_id`, `create_by`, `create_date`,
					// `update_id`, `update_by`, `update_date`, `datarule`)
					// VALUES
					// ('CAE479E12C500001C1BD539016A06AC0', 3, 20, '销售模块',
					// 'storeoutmodel', 5, '销售订单管理', 'salesorderdata', 1, 27,
					// '查看商品BOM', 'salesorderdata:showbow', NULL,
					// 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]',
					// '2024-09-11 10:36:25',
					// 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]',
					// '2024-09-11 10:36:41', 1),
					// ('CAE48517B160000134B018C0430E92B0', 3, 35, '财务模块',
					// 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 27, '上传附件',
					// 'inoutmanage:mainfileupload', '',
					// 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]',
					// '2024-09-11 13:52:22',
					// 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]',
					// '2024-09-11 14:00:37', 1),
					// ('CAE48590AB3000018C144DF016202950', 3, 35, '财务模块',
					// 'cashiermodel', 5, '收支管理', 'inoutmanage', 1, 28, '查看附件',
					// 'inoutmanage:mainfileread', '',
					// 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]',
					// '2024-09-11 14:00:38',
					// 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]',
					// '2024-09-11 14:00:54', 1),

					ps.addBatch("update  storeoutdetail s ,storeout m set s.originalbill=m.originalbill where s.storeoutid=m.storeoutid");
					ps.addBatch("update  storeindetail s ,storein m set s.originalbill=m.originalbill where s.storeinid=m.storeinid");
					ps.addBatch("update  outsourcingindetail s ,outsourcingin m set s.originalbill=m.originalbill where s.outsourcinginid=m.outsourcinginid");

					ps.addBatch("update  processinoutdetail s ,processinout m set s.originalbill=m.originalbill where s.processinoutid=m.processinoutid");
					ps.addBatch("update  prodrequisitiondetail s ,prodrequisition m set s.originalbill=m.originalbill where s.prodrequisitionid=m.prodrequisitionid");
					ps.addBatch("update  prodstoragedetail s ,prodstorage m set s.originalbill=m.originalbill where s.prodstorageid=m.prodstorageid");
					ps.addBatch("update  otherinoutdetail s ,otherinout m set s.originalbill=m.originalbill where s.otherinoutid=m.otherinoutid");

					ps.addBatch("update  itembegindetail s ,itembegin m set s.originalbill=m.originalbill where s.itembeginid=m.itembeginid");
					ps.addBatch("update  splitsdetail s ,splits m set s.originalbill=m.originalbill where s.splitsid=m.splitsid");

					ps.addBatch("update  stageoutsourcingdetail s ,stageoutsourcing m set s.originalbill=m.originalbill where s.stageoutsourcingid=m.stageoutsourcingid");
					ps.addBatch("update  storecheckdetail s ,storecheck m set s.originalbill=m.originalbill where s.storecheckid=m.storecheckid");
					ps.addBatch("update  storemovedetail s ,storemove m set s.originalbill=m.originalbill where s.storemoveid=m.storemoveid");

					ps.addBatch("create or replace view item_class_view as SELECT im.*,ifnull(cs.classname,'') as classname ,ifnull(tc.class_name,'') as class_name from iteminfo im left join itemclass cs on im.classid=cs.classid left join t_step_class tc on im.class_id=tc.id");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `itemtotal` ( `totalid` varchar(50) NOT NULL COMMENT '编号',  `companyid` varchar(50) DEFAULT NULL COMMENT '企业编号',  `itemid` varchar(50) DEFAULT NULL COMMENT '商品编号',  `houseid` varchar(50) DEFAULT NULL COMMENT '仓库',  `count` double DEFAULT '0' COMMENT '库存数量',  `money` double DEFAULT '0' COMMENT '库存金额',"
							+ " `checkout_count` double DEFAULT '0' COMMENT '备出库数量',  PRIMARY KEY (`totalid`),  UNIQUE KEY `com_item_house` (`itemid`,`houseid`),  KEY `companyid_1` (`companyid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("insert into itemtotal (totalid,companyid,itemid,houseid,count,money) select concat(itemid,'_',substring(houseid,length(houseid)-9)),im.companyid,im.itemid,im.houseid,round(sum(im.count),sc.countbit),round(sum(im.money),sc.moneybit) from itemmonth im,s_company_config sc where im.companyid=sc.company_id group by im.itemid,im.houseid");

					ps.addBatch("update itemtotal t,s_company_config sc set t.checkout_count=round(ifnull((select sum(im.checkout_count) from stock im where im.itemid=t.itemid and im.houseid=t.houseid) ,0),sc.countbit) where  t.companyid=sc.company_id");

					ps.addBatch("ALTER TABLE `itemmonth` DROP INDEX `sdate`, DROP INDEX `batchno`, DROP INDEX `houseid`, DROP INDEX `itemid`");
					ps.addBatch("ALTER TABLE `stock` DROP INDEX `itemid`, DROP INDEX `houseid`, DROP INDEX `batchno`");

					ps.addBatch("update prodstoragedetail pd,prodstorage p  set pd.orderid=p.orderid where p.prodstorageid=pd.prodstorageid and p.orderid<>pd.orderid");

					ps.addBatch("CREATE TRIGGER `itemmonth_after_insert` AFTER INSERT ON `itemmonth` FOR EACH ROW BEGIN "
							+ " insert into itemtotal (totalid,companyid,itemid,houseid,count,money,checkout_count) VALUES (concat(NEW.itemid,'_',substring(NEW.houseid,length(NEW.houseid)-9)),NEW.companyid,New.itemid,New.houseid,0,0,0) on duplicate key update companyid=NEW.companyid; "
							+ " update itemtotal t,s_company_config sc set t.count=round(ifnull((select sum(im.count) from itemmonth im where im.itemid=t.itemid and im.houseid=t.houseid) ,0),sc.countbit),t.money=round(ifnull((select sum(im.money) from itemmonth im where im.itemid=t.itemid and im.houseid=t.houseid) ,0),sc.moneybit) where t.itemid=New.itemid and t.houseid=New.houseid and t.companyid=sc.company_id;END");
					ps.addBatch("CREATE TRIGGER `itemmonth_after_update` AFTER UPDATE ON `itemmonth` FOR EACH ROW BEGIN update itemtotal t,s_company_config sc set t.count=round(ifnull((select sum(count) from itemmonth where itemid=t.itemid and houseid=t.houseid) ,0),sc.countbit),t.money=round(ifnull((select sum(money) from itemmonth where itemid=t.itemid and houseid=t.houseid) ,0),sc.moneybit) where t.itemid=New.itemid and t.houseid=New.houseid and t.companyid=sc.company_id;END");

					ps.addBatch("CREATE TRIGGER `stock_after_update` AFTER UPDATE ON `stock` FOR EACH ROW BEGIN update itemtotal t,s_company_config sc set t.checkout_count=round(ifnull((select sum(im.checkout_count) from stock im where im.itemid=t.itemid and im.houseid=t.houseid) ,0),sc.countbit) where t.itemid=New.itemid and t.houseid=New.houseid and t.companyid=sc.company_id;END");

					ps.addBatch("update exportdataset s set s.sno=25 where s.colid='total'");
					ps.addBatch("update exportdataset s set s.sno=26 where s.colid='remark'");
					ps.addBatch("update exportdataset s set s.sno=27 where s.colid='remark1'");
					ps.addBatch("update exportdataset s set s.sno=28 where s.colid='remark2'");
					ps.addBatch("update exportdataset s set s.sno=29 where s.colid='remark3'");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `changesales` INT(1) NOT NULL DEFAULT '0' COMMENT '销售订单管理的客户数据列显示合同单位名称' AFTER `caltype`");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth) select concat(substring(cb.companyid,length(cb.companyid)-11),substring(cb.customerid,length(cb.customerid)-11),DATE_FORMAT(cb.operate_time,'%Y%m01')),cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01'), year(cb.operate_time),month(cb.operate_time)  from storeout cb where cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customermonth c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.sdate=DATE_FORMAT(cb.operate_time,'%Y-%m-01') limit 1),false,true) group by cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01')");
					ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear) select concat(substring(cb.companyid,length(cb.companyid)-13),substring(cb.customerid,length(cb.customerid)-13),DATE_FORMAT(cb.operate_time,'%Y')),cb.companyid,cb.customerid,year(cb.operate_time) from storeout cb where cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customeryear c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.syear=year(cb.operate_time) limit 1),false,true) group by cb.companyid,cb.customerid,year(cb.operate_time)");

					ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth) select concat(substring(cb.companyid,length(cb.companyid)-11),substring(cb.customerid,length(cb.customerid)-11),DATE_FORMAT(cb.operate_time,'%Y%m01')),cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01'), year(cb.operate_time),month(cb.operate_time)  from storein cb where cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customermonth c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.sdate=DATE_FORMAT(cb.operate_time,'%Y-%m-01') limit 1),false,true) group by cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01')");
					ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear) select concat(substring(cb.companyid,length(cb.companyid)-13),substring(cb.customerid,length(cb.customerid)-13),DATE_FORMAT(cb.operate_time,'%Y')),cb.companyid,cb.customerid,year(cb.operate_time) from storein cb where cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customeryear c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.syear=year(cb.operate_time) limit 1),false,true) group by cb.companyid,cb.customerid,year(cb.operate_time)");

					ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth) select concat(substring(cb.companyid,length(cb.companyid)-11),substring(cb.customerid,length(cb.customerid)-11),DATE_FORMAT(cb.operate_time,'%Y%m01')),cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01'), year(cb.operate_time),month(cb.operate_time)  from outsourcingin cb where cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customermonth c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.sdate=DATE_FORMAT(cb.operate_time,'%Y-%m-01') limit 1),false,true) group by cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01')");
					ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear) select concat(substring(cb.companyid,length(cb.companyid)-13),substring(cb.customerid,length(cb.customerid)-13),DATE_FORMAT(cb.operate_time,'%Y')),cb.companyid,cb.customerid,year(cb.operate_time) from outsourcingin cb where cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customeryear c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.syear=year(cb.operate_time) limit 1),false,true) group by cb.companyid,cb.customerid,year(cb.operate_time)");

					ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth) select concat(substring(cb.companyid,length(cb.companyid)-11),substring(cb.customerid,length(cb.customerid)-11),DATE_FORMAT(cb.operate_time,'%Y%m01')),cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01'), year(cb.operate_time),month(cb.operate_time)  from stageoutsourcing cb where cb.bill_type='32' and cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customermonth c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.sdate=DATE_FORMAT(cb.operate_time,'%Y-%m-01') limit 1),false,true) group by cb.companyid,cb.customerid,DATE_FORMAT(cb.operate_time,'%Y-%m-01')");
					ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear) select concat(substring(cb.companyid,length(cb.companyid)-13),substring(cb.customerid,length(cb.customerid)-13),DATE_FORMAT(cb.operate_time,'%Y')),cb.companyid,cb.customerid,year(cb.operate_time) from stageoutsourcing cb where cb.bill_type='32' and cb.`status`='1' and cb.totalmoney!=0 and if((select 1 from customeryear c where c.companyid=cb.companyid and c.customerid=cb.customerid and c.syear=year(cb.operate_time) limit 1),false,true) group by cb.companyid,cb.customerid,year(cb.operate_time)");

					ps.addBatch("update customermonth cm,s_company_config sc set cm.rec_sellout_money=round(ifnull((select sum(cb.totalmoney) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='2' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.rec_sellin_money=round(ifnull((select sum(cb.totalmoney) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='7' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit)   where cm.companyid=sc.company_id");
					ps.addBatch("update customermonth cm,s_company_config sc set  cm.rec_add_money=round(cm.rec_sellout_money-cm.rec_sellin_money,sc.moneybit),cm.receivable=round(cm.rec_sellout_money-cm.rec_sellin_money-rec_money-rec_cmoney,sc.moneybit)  where cm.companyid=sc.company_id");
					ps.addBatch("update customeryear cm,s_company_config sc set cm.rec_sellout_money=round(ifnull((select sum(cb.totalmoney) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='2' and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.rec_sellin_money=round(ifnull((select sum(cb.totalmoney) from storeout cb where cb.customerid=cm.customerid and cb.bill_type='7' and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit)  where cm.companyid=sc.company_id");

					ps.addBatch("update customeryear cm,s_company_config sc set  cm.rec_add_money=round(cm.rec_sellout_money-cm.rec_sellin_money,sc.moneybit),cm.receivable=round(cm.rec_sellout_money-cm.rec_sellin_money-rec_money-rec_cmoney,sc.moneybit) where cm.companyid=sc.company_id");
					ps.addBatch("update customer cm,s_company_config sc set cm.receivable=round(ifnull((select sum(cb.receivable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.beginreceivable,sc.moneybit)  where cm.companyid=sc.company_id");
					ps.addBatch("update customermonth cm,s_company_config sc set cm.T_rec_sellout_money=round(ifnull((select sum(cb.taxmoney) from storeoutdetail cb where cb.customerid=cm.customerid and cb.stype='21' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) ,cm.T_rec_sellin_money=round(ifnull((select sum(cb.taxmoney) from storeoutdetail cb where cb.customerid=cm.customerid and cb.stype='71' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit)   where cm.companyid=sc.company_id");
					ps.addBatch("update customeryear cm,s_company_config sc set cm.T_rec_sellout_money=round(ifnull((select sum(cb.taxmoney) from storeoutdetail cb where cb.customerid=cm.customerid and cb.stype='21' and cm.syear=year(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) ,cm.T_rec_sellin_money=round(ifnull((select sum(cb.taxmoney) from storeoutdetail cb where cb.customerid=cm.customerid and cb.stype='71' and cm.syear=year(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit)  where cm.companyid=sc.company_id");

					ps.addBatch("update customermonth cm,s_company_config sc set  cm.T_rec_add_money=round(cm.T_rec_sellout_money-cm.T_rec_sellin_money,sc.moneybit),cm.T_receivable=round(cm.T_rec_sellout_money-cm.T_rec_sellin_money-T_rec_money-T_rec_cmoney,sc.moneybit)  where cm.companyid=sc.company_id");
					ps.addBatch("update customeryear cm,s_company_config sc set  cm.T_rec_add_money=round(cm.T_rec_sellout_money-cm.T_rec_sellin_money,sc.moneybit),cm.T_receivable=round(cm.T_rec_sellout_money-cm.T_rec_sellin_money-T_rec_money-T_rec_cmoney,sc.moneybit) where cm.companyid=sc.company_id");

					ps.addBatch("update customer cm,s_company_config sc set cm.T_receivable=round(ifnull((select sum(cb.T_receivable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.T_beginreceivable,sc.moneybit)  where cm.companyid=sc.company_id");

					ps.addBatch(" update customermonth cm,s_company_config sc set cm.pay_purchasein_money=round(ifnull((select sum(cb.totalmoney) from storein cb where cb.customerid=cm.customerid and cb.bill_type='1' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.pay_purchaseout_money=round(ifnull((select sum(cb.totalmoney) from storein cb where cb.customerid=cm.customerid and cb.bill_type='6' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) "
							+ ",cm.pay_outsourcing_money=round(ifnull((select sum(if(cb.bill_type='25',cb.totalmoney,-cb.totalmoney)) from outsourcingin cb where cb.customerid=cm.customerid and  cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0)+ifnull((select sum(cb.totalmoney) from stageoutsourcing cb where cb.customerid=cm.customerid  and cb.bill_type='32' and  cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) where cm.companyid=sc.company_id ");
					ps.addBatch(" update customermonth cm,s_company_config sc set  cm.pay_add_money=round(cm.pay_purchasein_money-cm.pay_purchaseout_money+cm.pay_outsourcing_money,sc.moneybit),cm.payable=round(cm.pay_purchasein_money+cm.pay_outsourcing_money-cm.pay_purchaseout_money-pay_money-pay_cmoney,sc.moneybit)  where cm.companyid=sc.company_id");

					ps.addBatch("update customeryear cm,s_company_config sc set cm.pay_purchasein_money=round(ifnull((select sum(cb.totalmoney) from storein cb where cb.customerid=cm.customerid and cb.bill_type='1' and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) ,cm.pay_purchaseout_money=round(ifnull((select sum(cb.totalmoney) from storein cb where cb.customerid=cm.customerid and cb.bill_type='6' and cm.syear=year(cb.operate_time) and cb.`status`='1'),0),sc.moneybit) "
							+ ",cm.pay_outsourcing_money=round(ifnull((select sum(if(cb.bill_type='25',cb.totalmoney,-cb.totalmoney)) from outsourcingin cb where cb.customerid=cm.customerid and  cm.syear=year(cb.operate_time) and cb.`status`='1'),0)+ifnull((select sum(cb.totalmoney) from stageoutsourcing cb where cb.customerid=cm.customerid  and cb.bill_type='32' and  cm.syear=year(cb.operate_time)  and cb.`status`='1'),0),sc.moneybit)  where cm.companyid=sc.company_id");

					ps.addBatch("update customeryear cm,s_company_config sc set  cm.pay_add_money=round(cm.pay_purchasein_money+cm.pay_outsourcing_money-cm.pay_purchaseout_money,sc.moneybit),cm.payable=round(cm.pay_purchasein_money+cm.pay_outsourcing_money-cm.pay_purchaseout_money-pay_money-pay_cmoney,sc.moneybit) where cm.companyid=sc.company_id");
					ps.addBatch("update customer cm,s_company_config sc set cm.payable=round(ifnull((select sum(cb.payable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.beginpayable,sc.moneybit)  where cm.companyid=sc.company_id");
					ps.addBatch(" update customermonth cm,s_company_config sc set cm.T_pay_purchasein_money=round(ifnull((select sum(cb.taxmoney) from storeindetail cb where cb.customerid=cm.customerid and cb.stype='11' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) ,cm.T_pay_purchaseout_money=round(ifnull((select sum(cb.taxmoney) from storeindetail cb where cb.customerid=cm.customerid and cb.stype='61' and cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) "
							+ ",cm.T_pay_outsourcing_money=round(ifnull((select sum(if(cb.stype='251',cb.taxmoney,-cb.taxmoney)) from outsourcingindetail cb where cb.customerid=cm.customerid and  cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0)+ifnull((select sum(cb.taxmoney) from stageoutsourcingdetail cb where cb.customerid=cm.customerid  and cb.stype='321' and  cm.syear=year(cb.operate_time) and cm.smonth=month(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) "
							+ " where cm.companyid=sc.company_id");
					ps.addBatch(" update customeryear cm,s_company_config sc set cm.T_pay_purchasein_money=round(ifnull((select sum(cb.taxmoney) from storeindetail cb where cb.customerid=cm.customerid and cb.stype='11' and cm.syear=year(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) ,cm.T_pay_purchaseout_money=round(ifnull((select sum(cb.taxmoney) from storeindetail cb where cb.customerid=cm.customerid and cb.stype='61' and cm.syear=year(cb.operate_time) and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit) "
							+ ",cm.T_pay_outsourcing_money=round(ifnull((select sum(if(cb.stype='251',cb.taxmoney,-cb.taxmoney)) from outsourcingindetail cb where cb.customerid=cm.customerid and  cm.syear=year(cb.operate_time)  and cb.`status`='1' and cb.taxrate>0),0)+ifnull((select sum(cb.taxmoney) from stageoutsourcingdetail cb where cb.customerid=cm.customerid  and cb.stype='321' and  cm.syear=year(cb.operate_time)  and cb.`status`='1' and cb.taxrate>0),0),sc.moneybit)"
							+ " where cm.companyid=sc.company_id");

					ps.addBatch(" update customermonth cm,s_company_config sc set  cm.T_pay_add_money=round(cm.T_pay_purchasein_money+cm.T_pay_outsourcing_money-cm.T_pay_purchaseout_money,sc.moneybit),cm.T_payable=round(cm.T_pay_purchasein_money+cm.T_pay_outsourcing_money-cm.T_pay_purchaseout_money-T_pay_money-T_pay_cmoney,sc.moneybit)  where cm.companyid=sc.company_id");
					ps.addBatch(" update customeryear cm,s_company_config sc set  cm.T_pay_add_money=round(cm.T_pay_purchasein_money+cm.T_pay_outsourcing_money-cm.T_pay_purchaseout_money,sc.moneybit),cm.T_payable=round(cm.T_pay_purchasein_money+cm.T_pay_outsourcing_money-cm.T_pay_purchaseout_money-T_pay_money-T_pay_cmoney,sc.moneybit) where cm.companyid=sc.company_id");

					ps.addBatch(" update customer cm,s_company_config sc set cm.T_payable=round(ifnull((select sum(cb.T_payable) from customeryear cb where cb.customerid=cm.customerid),0)+cm.T_beginpayable,sc.moneybit)  where cm.companyid=sc.company_id");

				}

				if (version < 2.5 && newversion >= 2.5) {
					ps.addBatch("ALTER TABLE `outsourcing` ADD COLUMN `customerstatus` INT(1) NOT NULL DEFAULT '2' COMMENT '加工单位查看' AFTER `contractorderid`, ADD COLUMN `cmconfirmdate` DATETIME NULL DEFAULT NULL COMMENT '加工单位确认时间' AFTER `customerstatus`, ADD COLUMN `cmconfirmremark` VARCHAR(500) NULL DEFAULT '' COMMENT '加工单位备注' AFTER `cmconfirmdate`, ADD INDEX `customerstatus` (`customerstatus`), ADD INDEX `cmconfirmdate` (`cmconfirmdate`), ADD INDEX `audit_time` (`audit_time`), ADD INDEX `contractid` (`contractid`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `sendmessage` (  `id` varchar(32) NOT NULL,   `companyid` varchar(32) NOT NULL,  `bill_type` varchar(3) NOT NULL,  `customerid` varchar(32) NOT NULL, `billid` varchar(32) NOT NULL DEFAULT '',"
							+ "`detailid` varchar(32) NOT NULL DEFAULT '',  `fid` varchar(32) NOT NULL DEFAULT '', `billorderid` varchar(50) NOT NULL DEFAULT '', `sendtype` int(1) NOT NULL DEFAULT '0' COMMENT '1-我方 2-往来单位', `cmname` varchar(100) NOT NULL DEFAULT '', `myname` varchar(100) NOT NULL DEFAULT '',"
							+ " `sendinfo` varchar(2000) NOT NULL DEFAULT '',  `senddate` datetime DEFAULT NULL,  `senduserid` varchar(32) DEFAULT '',  `sendusername` varchar(50) DEFAULT '',  `confirmdate` datetime DEFAULT NULL,  `confirmuserid` varchar(32) DEFAULT '',  `confirmusername` varchar(50) DEFAULT '',  `confirminfo` varchar(100) DEFAULT '',"
							+ "`status` int(1) DEFAULT '1', PRIMARY KEY (`id`),  KEY `companyid` (`companyid`),  KEY `bill_type` (`bill_type`),   KEY `customerid` (`customerid`), KEY `billid` (`billid`),  KEY `senddate` (`senddate`),  KEY `confirmdate` (`confirmdate`),  KEY `status` (`status`),  KEY `fid` (`fid`), KEY `detailid` (`detailid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("ALTER TABLE `outsourcingdetail` ADD COLUMN `sendcount` DOUBLE NULL DEFAULT '0' COMMENT '当前发货数量' AFTER `checkout_count`, ADD COLUMN `hascount` DOUBLE NULL DEFAULT '0' COMMENT '加工单位存货量' AFTER `sendcount`, ADD COLUMN `hasremark` VARCHAR(1000) NULL DEFAULT '' COMMENT '加工单位备注' AFTER `hascount`, ADD COLUMN `myremark` VARCHAR(1000) NULL DEFAULT '' COMMENT '我方备注' AFTER `hasremark`,"
							+ "ADD COLUMN `hasdate` DATETIME NULL DEFAULT NULL COMMENT '加工方最后修改时间' AFTER `myremark`, ADD COLUMN `mydate` DATETIME NULL DEFAULT NULL COMMENT '我方最后修改时间' AFTER `hasdate`, ADD COLUMN `myuserid` VARCHAR(32) NULL DEFAULT '' COMMENT '我方修改人id' AFTER `mydate`, ADD COLUMN `myusername` VARCHAR(50) NULL DEFAULT '' COMMENT '我方修改人' AFTER `myuserid`, ADD COLUMN `cmincount` DOUBLE NULL DEFAULT '0' COMMENT '加工商入库数量' AFTER `myusername`,ADD COLUMN `cmoutcount` DOUBLE NULL DEFAULT '0' COMMENT '加工商出库数量' AFTER `cmincount`,ADD COLUMN `cmpackagecount` DOUBLE NULL DEFAULT '0' COMMENT '加工商包装数量' AFTER `cmoutcount`");

					ps.addBatch("ALTER TABLE `s_company_config` "
							+ "ADD COLUMN `visitcolor1` VARCHAR(10) NULL DEFAULT '#00a65a' COMMENT '供应商访问端表头颜色' AFTER `visitcolor`,"
							+ "ADD COLUMN `visitcolor2` VARCHAR(10) NULL DEFAULT '#f39c12' COMMENT '加工商访问端表头颜色' AFTER `visitcolor1`,"
							+ "ADD COLUMN `visititle1` VARCHAR(100) NULL DEFAULT '采购订单跟踪进度' COMMENT '供应商访问端表头' AFTER `visititle`,"
							+ "ADD COLUMN `visititle2` VARCHAR(100) NULL DEFAULT '委外订单跟踪进度' COMMENT '加工商访问端表头' AFTER `visititle1`,"
							+ "ADD COLUMN `visitmonth1` INT(11) NULL DEFAULT '6' COMMENT '供应商访问月份' AFTER `visitmonth`, 	ADD COLUMN `visitmonth2` INT(11) NULL DEFAULT '6' COMMENT '加工商访问月份' AFTER `visitmonth1`,"
							+ "ADD COLUMN `visitexport1` INT(1) NULL DEFAULT '0' COMMENT '导出采购订单数据' AFTER `visitexport`, ADD COLUMN `visitexport2` INT(1) NULL DEFAULT '0' COMMENT '导出委外订单数据' AFTER `visitexport1`, ADD COLUMN `visitproperty1` INT(1) NULL DEFAULT '0' COMMENT '显示采购商品属性' AFTER `visitproperty`, ADD COLUMN `visitproperty2` INT(1) NULL DEFAULT '0' COMMENT '显示委外商品属性' AFTER `visitproperty1`,"
							+ "ADD COLUMN `visitlimt` INT(1) NULL DEFAULT '1' COMMENT '开放客户访问端' AFTER `visitproperty2`, ADD COLUMN `visitlimt1` INT(1) NULL DEFAULT '0' COMMENT '开放供应商访问端' AFTER `visitlimt`, ADD COLUMN `visitlimt2` INT(1) NULL DEFAULT '0' COMMENT '开放加工商访问端' AFTER `visitlimt1`,"
							+ "ADD COLUMN `visitalllist` INT(1) NULL DEFAULT '1' COMMENT '默认客户可查已审的单权限为开放不需确认' AFTER `visitlimt2`,ADD COLUMN `visitalllist1` INT(1) NULL DEFAULT '0' COMMENT '默认供应商查已审的单权限为开放不需确认' AFTER `visitalllist`,ADD COLUMN `visitalllist2` INT(1) NULL DEFAULT '0' COMMENT '默认加工商查已审的单权限为开放不需确认' AFTER `visitalllist1`");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `customerinoutdetail` (  `id` varchar(32) NOT NULL,  `companyid` varchar(32) NOT NULL DEFAULT '',   `stype` varchar(3) NOT NULL DEFAULT '',  `customerid` varchar(32) NOT NULL DEFAULT '',  `billid` varchar(32) NOT NULL DEFAULT '',  `detailid` varchar(32) NOT NULL DEFAULT '',   `billorderid` varchar(50) NOT NULL DEFAULT '',  `inouttype` int(1) NOT NULL DEFAULT '0' COMMENT '1-入库 2-出库 3-退回', `batchno` varchar(50) NOT NULL DEFAULT '', `outcount` double NOT NULL DEFAULT 0,   `incount` double NOT NULL DEFAULT 0,   `cm_date` datetime DEFAULT NULL,    `remark` varchar(300) DEFAULT '' ,   PRIMARY KEY (`id`),  KEY `companyid` (`companyid`),  KEY `stype` (`stype`),   KEY `customerid` (`customerid`),   KEY `billid` (`billid`),   KEY `cm_date` (`cm_date`),    KEY `inouttype` (`inouttype`),   KEY `detailid` (`detailid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("update exportdataset s set s.sno=30,s.isshow=1,s.engilishcolname='Total amount (excl. tax)',s.chinesecolname='不含税总金额',s.colname='不含税总金额(订单数量*单价)',s.swidth=14 where s.colid='total'");

					ps.addBatch("ALTER TABLE `accountbill`   ADD COLUMN `honourstype` INT NULL DEFAULT '0' COMMENT '票据种类' AFTER `invoicedate`, ADD COLUMN `honourcompany` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '出票单位' AFTER `honourstype`, ADD COLUMN `honourbank` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '承兑银行' AFTER `honourcompany`, ADD COLUMN `beginDate` DATE NULL DEFAULT NULL COMMENT '出票日期' AFTER `honourbank`,"
							+ "ADD COLUMN `endDate` DATE NULL DEFAULT NULL COMMENT '到期日期' AFTER `beginDate`, ADD COLUMN `honourDate` DATE NULL DEFAULT NULL COMMENT '承兑日期' AFTER `endDate`, ADD COLUMN `honouruser` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '承兑人' AFTER `honourDate`, ADD COLUMN `smoney` DOUBLE NULL DEFAULT '0' COMMENT '承兑金额' AFTER `honouruser`, ADD COLUMN `honourstatus` INT NULL DEFAULT '1' COMMENT '承兑状态' AFTER `smoney`, ADD COLUMN `honourremark` VARCHAR(200) NULL DEFAULT '0' COMMENT '票据备注' AFTER `honourstatus`, ADD INDEX `endDate` (`endDate`), ADD INDEX `beginDate` (`beginDate`), ADD INDEX `honourDate` (`honourDate`)");

					ps.addBatch("update processinoutdetail p,outsourcingdetail o set p.relationdetailid=o.detailid where p.relationmainid=o.outsourcingid and p.itemid=o.itemid and p.stype='241' and p.relationdetailid='null'");

					ps.addBatch("ALTER TABLE `accountbill`   CHANGE COLUMN `honourcompany` `honourcompany` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '出票人' AFTER `honourstype`, CHANGE COLUMN `honourbank` `honourbank` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '承兑人' AFTER `honourcompany`, CHANGE COLUMN `honourDate` `honourDate` DATE NULL DEFAULT NULL COMMENT '付款日期' AFTER `endDate`, CHANGE COLUMN `honouruser` `honouruser` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '收款人' AFTER `honourDate`, CHANGE COLUMN `smoney` `smoney` DOUBLE NULL DEFAULT '0' COMMENT '实际金额' AFTER `honouruser`, ADD COLUMN `honourDiscount` DOUBLE NULL DEFAULT '0' COMMENT '贴现' AFTER `smoney`");

					ps.addBatch("ALTER TABLE `accountbill` ADD INDEX `honourstatus` (`honourstatus`), ADD INDEX `operate_time` (`operate_time`), ADD INDEX `originalbill` (`originalbill`)");

					ps.addBatch("update accountbill a set a.honourstatus=1 where a.bill_type in ('18','19')");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ " ('CAEF155F25E000014AC160821783187F', 4, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 21, '开放加工方可查看已核单的设置', 'outsourcingdata:osexchangeinfo_set', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:33:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:36:41', 1),"
							+ "('CAEF159B9EF000018F265820F98A10CE', 4, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 22, '编辑加工方可看的备注', 'outsourcingdata:osexchangeinfo_remark', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:37:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:38:15', 1),"
							+ "('CAEF15A7B66000019663D6151EB32E20', 4, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 23, '查看加工方反馈信息', 'outsourcingdata:osexchangeinfo_feedback', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:38:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:39:32', 1),"
							+ "('CAEF15C05FA00001E47FC52013906F00', 4, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 24, '查看与加工方交流信息', 'outsourcingdata:osexchangeinfo_showexchange', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:39:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:40:47', 1),"
							+ "('CAEF15D1D4900001F9A993001F8B13A4', 4, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 25, '发送与加工方交流信息', 'outsourcingdata:osexchangeinfo_sendexchange', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:40:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:41:26', 1),"
							+ "('CAEF15E260800001B5B218807EBB75A0', 4, 28, '委外加工', 'outsourcingmodel', 2, '委外加工管理', 'outsourcingdata', 1, 26, '删除与加工方交流信息', 'outsourcingdata:osexchangeinfo_delexchange', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:42:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-10-14 09:42:34', 1),"
							+ "('CAF6947C48D0000165531A1517201FF5', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 20, '上传承兑附件', 'honourmanage:mainfileupload', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:32:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:41:17', 1),"
							+ "('CAF694853E400001A1F07F411D6B1823', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 1, '查看', 'honourmanage:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:32:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:41:47', 1),"
							+ "('CAF69490A730000199DC667298201478', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 2, '新增收承兑', 'honourmanage:newbillin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:33:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:41:49', 1),"
							+ "('CAF6949863B00001F6317CF073B01569', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 6, '导出收承兑', 'honourmanage:exportbillin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:33:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:41:59', 1),"
							+ "('CAF6949E83400001A8BF970093581250', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 4, '复制收承兑', 'honourmanage:copybillin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:34:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:41:55', 1),"
							+ "('CAF694A662300001329473E0125E110D', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 5, '作废收承兑', 'honourmanage:statusbillin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:34:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:41:58', 1),"
							+ "('CAF694ADC8300001F871C5801E97C110', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 10, '新增付承兑', 'honourmanage:newbillout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:35:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:40:21', 1),"
							+ "('CAF694B2BE400001347A18B01AF47C20', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 11, '修改付承兑', 'honourmanage:modifybillout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:35:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:40:26', 1),"
							+ "('CAF694B6753000011287A8FA168016D5', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 12, '复制付承兑', 'honourmanage:copybillout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:35:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:40:29', 1),"
							+ "('CAF694C0D9C0000161A01F401CE65900', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 3, '修改收承兑', 'honourmanage:modifybillin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:36:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:41:51', 1),"
							+ "('CAF694CC7CA00001FA665E901C72F590', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 13, '作废付承兑', 'honourmanage:statusbillout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:37:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:40:35', 1),"
							+ "('CAF694D2E0800001EE80CDCE9A4BE800', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 14, '导出付承兑', 'honourmanage:exporbillout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:37:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:40:38', 1),"
							+ "('CAF694DBE1F00001D2BE64801BC61662', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 19, '查看承兑附件', 'honourmanage:mainfileread', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-06 16:38:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:41:13', 1),"
							+ "('CAF86EB12B5000018B8329F010F01770', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 7, '打印收承兑', 'honourmanage:printbillin', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:39:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:39:56', 1),"
							+ "('CAF86EC4EB000001894C11B23392B390', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 15, '打印付承兑', 'honourmanage:printbillout', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:40:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 10:41:06', 1),"
							+ "('CAF87B39FEC0000190661C0F17B0AFB0', 3, 35, '财务模块', 'cashiermodel', 6, '承兑管理', 'honourmanage', 1, 22, '导出承兑预警记录', 'honourmanage:exportall', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 14:18:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-12 14:19:24', 1)");

					ps.addBatch("update  customer c,s_company_config sc  set c.T_receivable=round(c.T_beginreceivable+ifnull((select sum(cm.T_receivable) from customermonth cm where cm.customerid=c.customerid),0),sc.moneybit),c.T_payable=round(c.T_beginpayable+ifnull((select sum(cm.T_payable) from customermonth cm where cm.customerid=c.customerid),0),sc.moneybit) where c.companyid=sc.company_id ");// and
																																																																																																					// (c.T_receivable<>0

					ps.addBatch("update s_permission s set s.fname=REPLACE(s.fname,'客户端','往来单位端')  where s.functionvalue='customerdata' and s.fname like '%客户端%'");

					ps.addBatch("update exportdataset s set s.sno=-1  where s.colid='sno'");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `billprodreqset` INT(1) NOT NULL DEFAULT '0' COMMENT '生产领料选工单后，不带出配置物料，扫商品码带出物料并验证' AFTER `changesales`");

					ps.addBatch("update purchaseorderdetail o, s_company_config c set  o.tax = if(o.taxrate>0,round((o.taxmoney-o.total),c.moneybit),0)  where   o.companyid=c.company_id");
					ps.addBatch("update purchaseorder o,s_company_config c set o.totaltax=round(ifnull((select sum(od.tax) from purchaseorderdetail od where o.purchaseorderid=od.purchaseorderid),0),c.moneybit)  where   o.companyid=c.company_id");

					ps.addBatch("update outsourcingdetail o, s_company_config c set  o.tax = if(o.taxrate>0,round((o.taxmoney-o.total),c.moneybit),0)  where   o.companyid=c.company_id  and o.stype='221' ");
					ps.addBatch("update outsourcing o,s_company_config c set o.totaltax=round(ifnull((select sum(od.tax) from outsourcingdetail od where o.outsourcingid=od.outsourcingid),0),c.moneybit)  where   o.companyid=c.company_id");

					ps.addBatch("ALTER TABLE `stageoutsourcingdetail` ADD COLUMN `isreturn` INT(1) NULL DEFAULT '0' COMMENT '是否返工' AFTER `remark`, ADD INDEX `isreturn` (`isreturn`)");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES"
							+ "('CAFCAE94E9700001852B1CF0161E1A93', 3, 40, '报表模块', 'reportmodel', 30, '库存状况', 'stockstatedata', 1, 10, '导出数据', 'stockstatedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-25 15:31:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-11-25 15:31:57', 1),"
							+ "('CB0411A7D640000138674EA010A218B8', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 14, '商品附件查看', 'purchasedata:itemuploadshow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:20:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:22:13', 1),"
							+ "('CB0411B7983000011E5C1E37A0401272', 3, 10, '采购模块', 'storeinmodel', 5, '采购订单管理', 'purchaseorderdata', 1, 21, '商品附件查看', 'purchaseorderdata:itemuploadshow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:21:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:22:03', 1),"
							+ "('CB0411C3481000019ECC82A215791587', 3, 10, '采购模块', 'storeinmodel', 10, '采购入库管理', 'storeindata', 1, 24, '商品附件查看', 'storeindata:itemuploadshow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:22:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:59:49', 1),"
							+ "('CB0411C9114000018EEC1A201AE51371', 3, 10, '采购模块', 'storeinmodel', 20, '采购退货管理', 'storeinoutdata', 1, 23, '商品附件查看', 'storeinoutdata:itemuploadshow', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:22:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-12-18 14:23:03', 1)");

					// 删除重复的导入往来单位权限
					ps.addBatch("delete from s_permission where id='C87A4599F25000017799E742B2BD1C48'");
					ps.addBatch("delete from s_roles_permission where functionid = 'C87A4599F25000017799E742B2BD1C48'");

					ps.addBatch("ALTER TABLE `outsourcing` ADD COLUMN `cmstatusdate` DATETIME NULL DEFAULT NULL COMMENT '开放时间' AFTER `cmconfirmremark`, ADD COLUMN `cmstatus_id` VARCHAR(36) NULL DEFAULT '' COMMENT '开放人id' AFTER `cmstatusdate`, ADD COLUMN `cmstatus_by` VARCHAR(50) NULL DEFAULT '' COMMENT '开放人' AFTER `cmstatus_id`");

					ps.addBatch("update s_permission s set s.fname='变更开放加工商查看权限'  where s.fvalue='outsourcingdata:osexchangeinfo_set'");

					ps.addBatch("ALTER TABLE `t_order_progress` ADD INDEX `audit_id` (`audit_id`)");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `houselist` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '不计算的仓库列表' AFTER `billprodreqset`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

				}

				if (version < 2.6 && newversion >= 2.6) {
					ps.addBatch("ALTER TABLE `iteminfo` CHANGE COLUMN `create_by` `create_by` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建人' AFTER `create_id`, ADD COLUMN `quality` INT(1) NULL DEFAULT '0' COMMENT '质检配置' AFTER `filesize`, 	ADD COLUMN `qualityremark` VARCHAR(500) NULL DEFAULT '' COMMENT '质检说明' AFTER `quality`, ADD COLUMN `qcreate_id` VARCHAR(36) NULL DEFAULT '' COMMENT '创建人ID' AFTER `qualityremark`, ADD COLUMN `qcreate_by` VARCHAR(50) NULL DEFAULT '' COMMENT '创建人' AFTER `qcreate_id`, ADD COLUMN `qcreate_time` DATETIME NULL DEFAULT NULL COMMENT '创建时间' AFTER `qcreate_by`, ADD COLUMN `qupdate_id` VARCHAR(36) NULL DEFAULT '' COMMENT '更新人ID' AFTER `qcreate_time`, ADD COLUMN `qupdate_by` VARCHAR(50) NULL DEFAULT '' COMMENT '更新人' AFTER `qupdate_id`, ADD COLUMN `qupdate_time` DATETIME NULL DEFAULT NULL COMMENT '更新时间' AFTER `qupdate_by`, ADD INDEX `quality` (`quality`), ADD INDEX `qcreate_time` (`qcreate_time`), ADD INDEX `qupdate_time` (`qupdate_time`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualityclass` ( `classid` varchar(36) NOT NULL COMMENT '主键',"
							+ " `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `classname` varchar(20) NOT NULL DEFAULT '' COMMENT '质检分类',  `parentid` varchar(36) NOT NULL DEFAULT '' COMMENT '父级',  `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建日期',"
							+ "`update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '更新人ID',  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新日期', PRIMARY KEY (`classid`),  KEY `companyid` (`companyid`),  KEY `create_id` (`create_id`),   KEY `update_id` (`update_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质检项分类'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualityitem` ( `qitemid` varchar(36) NOT NULL COMMENT '编号', `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `qclassid` varchar(36) NOT NULL DEFAULT '' COMMENT '质检项分类',  `qcodeid` varchar(50) NOT NULL DEFAULT '' COMMENT '检验编号',  `qitemname` varchar(100) NOT NULL DEFAULT '' COMMENT '检验项目',"
							+ "`toleranceup` varchar(20) NOT NULL DEFAULT '' COMMENT '公差上限', `tolerancedown` varchar(20) NOT NULL DEFAULT '' COMMENT '公差下限', `device` varchar(100) NOT NULL DEFAULT '' COMMENT '检验方式', `qcontent` varchar(200) NOT NULL DEFAULT '' COMMENT '检验内容',  `eligibility` varchar(200) NOT NULL DEFAULT '' COMMENT '合格标准',  `testbasic` varchar(200) NOT NULL DEFAULT '' COMMENT '检验依据',  `judgingcondition` int(1) NOT NULL DEFAULT '1' COMMENT '判断条件',"
							+ "`uplimit` varchar(50) NOT NULL DEFAULT '' COMMENT '上限值(合格值)', `downlimt` varchar(50) NOT NULL DEFAULT '' COMMENT '下限值',  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',  `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',   `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建日期',"
							+ "`update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新日期',  PRIMARY KEY (`qitemid`),  KEY `companyid` (`companyid`),  KEY `qclassid` (`qclassid`),  KEY `qcodeid` (`qcodeid`),  KEY `create_id` (`create_id`),  KEY `update_id` (`update_id`),  KEY `judgingcondition` (`judgingcondition`), KEY `device` (`device`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质检项'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualityiteminfo` ( `qtid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `itemid` varchar(36) NOT NULL DEFAULT '' COMMENT '商品ID', `sno` int(11) NOT NULL DEFAULT '1' COMMENT '序号', `qitemname` varchar(100) NOT NULL DEFAULT '' COMMENT '检验项目',"
							+ "`toleranceup` varchar(50) NOT NULL DEFAULT '' COMMENT '公差上限',  `tolerancedown` varchar(50) NOT NULL DEFAULT '' COMMENT '公差下限',  `device` varchar(100) NOT NULL DEFAULT '' COMMENT '检验方式', `qcontent` varchar(100) NOT NULL DEFAULT '' COMMENT '检验内容', `eligibility` varchar(100) NOT NULL DEFAULT '' COMMENT '合格标准',  `testbasic` varchar(100) NOT NULL DEFAULT '' COMMENT '检验依据',"
							+ " `judgingcondition` int(1) NOT NULL DEFAULT '1' COMMENT '判断条件',  `uplimit` varchar(50) NOT NULL DEFAULT '' COMMENT '上限值(合格值)',  `downlimt` varchar(50) NOT NULL DEFAULT '' COMMENT '下限值',  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',  `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',"
							+ " `create_time` datetime DEFAULT NULL COMMENT '创建日期',   `update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新日期',  PRIMARY KEY (`qtid`),  UNIQUE KEY `companyid_3` (`companyid`,`itemid`,`sno`),  KEY `create_id` (`create_id`),  KEY `update_id` (`update_id`),  KEY `device` (`device`),  KEY `qitemname` (`qitemname`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品质检项'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualitytype` (  `id` varchar(36) NOT NULL COMMENT '主键', `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `qualitynum` int(11) NOT NULL DEFAULT '0' COMMENT '顺序号', `qualityname` varchar(20) NOT NULL DEFAULT '' COMMENT '质量问题', `qualitytype` int(1) NOT NULL DEFAULT '1' COMMENT '质量类别',"
							+ " `fstatus` int(1) NOT NULL DEFAULT '1' COMMENT '状态',  `remark` varchar(100) NOT NULL DEFAULT '' COMMENT '备注',  `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建日期',  `update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '更新人ID',  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',"
							+ "`update_time` datetime DEFAULT NULL COMMENT '更新日期',  PRIMARY KEY (`id`),  KEY `companyid` (`companyid`),  KEY `qualitytype` (`qualitytype`),  KEY `fstatus` (`fstatus`),  KEY `create_id` (`create_id`),  KEY `update_id` (`update_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质量问题'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ " ('CB08D67DA4A00001CD3D1050E20E5880', 3, 29, '质量模块', 'qualitiedmodel', 40, '质检问题管理', 'qualitytypedata', 1, 1, '查看', 'qualitytypedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:56:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:00:17', 1),"
							+ "('CB08D6B4CF200001273BC3501A5F113A', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 1, '查看', 'qualityitemdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:59:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:59:57', 1),"
							+ "('CB08D6EEB9900001BA3411901BAB29C0', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 2, '新增', 'qualityitemdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:03:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:04:08', 1),"
							+ "('CB08D6F2698000018AEE21502BD97FB0', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 3, '编辑', 'qualityitemdata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:04:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-07 10:42:24', 1),"
							+ "('CB08D6F56670000184614C88F560FC40', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 4, '批量删除', 'qualityitemdata:delete', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:04:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-07 10:42:31', 1),"
							+ "('CB08D6FB8F8000018C7819BAD0002560', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 5, '复制', 'qualityitemdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:04:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:05:00', 1),"
							+ "('CB08D70029D00001CE7519BA1D921C54', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 6, '新增分类', 'qualityitemdata:newclass', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:05:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:05:23', 1),"
							+ "('CB08D704CF000001AE1D19101CFB1D3D', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 8, '删除分类', 'qualityitemdata:delclass', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:05:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:05:51', 1),"
							+ "('CB08D70A22C00001C4CD91501056130A', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 7, '修改分类', 'qualityitemdata:editclass', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:05:46', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:06:04', 1),"
							+ "('CB08D713008000017DF81B7C78A01A9F', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 9, '批量变更分类', 'qualityitemdata:changeclas', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:06:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:06:50', 1),"
							+ "('CB08D71A5640000177F615A01F8ADB70', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 10, '导入数据', 'qualityitemdata:import', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:06:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:07:22', 1),"
							+ "('CB08D72230400001C4751D28795442F0', 3, 29, '质量模块', 'qualitiedmodel', 30, '质检项管理', 'qualityitemdata', 1, 11, '导出数据', 'qualityitemdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:07:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:07:40', 1),"
							+ "('CB08D74D28D000014D29618B1880168B', 3, 29, '质量模块', 'qualitiedmodel', 40, '质检问题管理', 'qualitytypedata', 1, 2, '新增', 'qualitytypedata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:10:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:10:34', 1),"
							+ "('CB08D7509A90000167C71AD0110F87B0', 3, 29, '质量模块', 'qualitiedmodel', 40, '质检问题管理', 'qualitytypedata', 1, 3, '编辑', 'qualitytypedata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:10:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:10:54', 1),"
							+ "('CB08D75569500001BABAA17F12C01BC6', 3, 29, '质量模块', 'qualitiedmodel', 40, '质检问题管理', 'qualitytypedata', 1, 4, '启/停用', 'qualitytypedata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:10:55', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:12:01', 1),"
							+ "('CB08D769ED600001C8A31BF01FB01230', 3, 29, '质量模块', 'qualitiedmodel', 40, '质检问题管理', 'qualitytypedata', 1, 5, '导出数据', 'qualitytypedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:12:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:12:39', 1),"
							+ "('CB0B15C1C5B0000127B1317013301FF4', 3, 29, '质量模块', 'qualitiedmodel', 25, '商品质检配置', 'qualityiteminfodata', 1, 1, '查看', 'qualityiteminfodata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:29:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:30:30', 1),"
							+ "('CB0B15CEA1700001516A1F9DCDD21766', 3, 29, '质量模块', 'qualitiedmodel', 25, '商品质检配置', 'qualityiteminfodata', 1, 6, '导出数据', 'qualityiteminfodata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:30:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:32:53', 1),"
							+ "('CB0B15DA9C7000016BD71C687500BF50', 3, 29, '质量模块', 'qualitiedmodel', 25, '商品质检配置', 'qualityiteminfodata', 1, 2, '新增', 'qualityiteminfodata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:31:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:31:23', 1),"
							+ "('CB0B15E08BE00001972AAEE71B909890', 3, 29, '质量模块', 'qualitiedmodel', 25, '商品质检配置', 'qualityiteminfodata', 1, 3, '编辑', 'qualityiteminfodata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:31:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:31:47', 1),"
							+ "('CB0B15E9CAD00001386B17D031F0B0A0', 3, 29, '质量模块', 'qualitiedmodel', 25, '商品质检配置', 'qualityiteminfodata', 1, 4, '批量删除', 'qualityiteminfodata:delete', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:32:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:32:25', 1),"
							+ "('CB0B15EE0060000137D619B07CEE5390', 3, 29, '质量模块', 'qualitiedmodel', 25, '商品质检配置', 'qualityiteminfodata', 1, 5, '导入数据', 'qualityiteminfodata:import', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:32:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-09 09:33:00', 1)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualitymain` ( `mainid` varchar(36) NOT NULL COMMENT '编号', `billtype` int(11) NOT NULL DEFAULT '1' COMMENT '质检类型', `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `orderid` varchar(36) NOT NULL DEFAULT '' COMMENT '检验单号',  `operate_time` date DEFAULT NULL COMMENT '单据日期', `operate_by` varchar(50) NOT NULL DEFAULT '' COMMENT '检验员',  `itemid` varchar(36) NOT NULL DEFAULT '' COMMENT '商品ID',  `batchno` varchar(50) NOT NULL DEFAULT '' COMMENT '批号',  `customerid` varchar(36) NOT NULL DEFAULT '' COMMENT '往来单位', `testtype` int(1) NOT NULL DEFAULT '1' COMMENT '检验类型',  `count` double NOT NULL DEFAULT '0' COMMENT '单据数量',  `samplecount` double NOT NULL DEFAULT '0' COMMENT '抽检数量',  `qualifiedcount` double NOT NULL DEFAULT '0' COMMENT '合格数量',"
							+ "`unqualifiedcount` double NOT NULL DEFAULT '0' COMMENT '不良数量',  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',  `remark1` varchar(200) NOT NULL DEFAULT '' COMMENT '备注1',  `remark2` varchar(200) NOT NULL DEFAULT '' COMMENT '备注2', `remark3` varchar(200) NOT NULL DEFAULT '' COMMENT '备注3', `qualityitemremark` varchar(500) NOT NULL DEFAULT '' COMMENT '商品质检说明', `judgingresult` int(1) NOT NULL DEFAULT '1' COMMENT '判断结果',  `isqualified` int(1) NOT NULL DEFAULT '1' COMMENT '是否合格',  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态',  `printing` int(11) NOT NULL DEFAULT '1' COMMENT '打印次数',  `outexcel` int(11) NOT NULL DEFAULT '1' COMMENT '导出次数',  `create_id` varchar(36) NOT NULL DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',   `create_time` datetime DEFAULT NULL COMMENT '创建日期',  `audit_id` varchar(36) NOT NULL DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) NOT NULL DEFAULT '' COMMENT '审核人',"
							+ "`audit_time` datetime DEFAULT NULL COMMENT '审核时间', `update_id` varchar(36) NOT NULL DEFAULT '' COMMENT '更新人ID',  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新日期', `relationdetailid` varchar(36) NOT NULL DEFAULT '' COMMENT '关联明细id',  `relationorderid` varchar(36) NOT NULL DEFAULT '' COMMENT '关联单号',  `relationmainid` varchar(36) NOT NULL DEFAULT '' COMMENT '关联单主表id',  `relationmaintatble` varchar(36) NOT NULL DEFAULT '' COMMENT '关联主表名',  `relationdetailtatble` varchar(36) NOT NULL DEFAULT '' COMMENT '关联明细表名',   `relationtablename` varchar(36) NOT NULL DEFAULT '' COMMENT '关联明细数据表名',"
							+ "`fvaluecount` int(11) NOT NULL DEFAULT '1' COMMENT '检测值数量', PRIMARY KEY (`mainid`), KEY `companyid` (`companyid`),  KEY `billtype` (`billtype`), KEY `itemid` (`itemid`), KEY `orderid` (`orderid`), KEY `operate_time` (`operate_time`),KEY `status` (`status`), KEY `isqualified` (`isqualified`), KEY `relationdetailid` (`relationdetailid`), KEY `relationmainid` (`relationmainid`), KEY `create_id` (`create_id`), KEY `update_id` (`update_id`), KEY `operate_by` (`operate_by`), KEY `customerid` (`customerid`), KEY `testtype` (`testtype`), KEY `judgingresult` (`judgingresult`),KEY `audit_id` (`audit_id`),KEY `relationorderid` (`relationorderid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质检单'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualitydetail` ( `detailid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号', `mainid` varchar(36) NOT NULL DEFAULT '' COMMENT '质检单ID', `itemid` varchar(36) NOT NULL DEFAULT '' COMMENT '商品ID', `operate_time` date DEFAULT NULL COMMENT '单据日期', `billtype` int(1) NOT NULL DEFAULT '1' COMMENT '质检类型',  `goods_number` int(11) NOT NULL DEFAULT '1' COMMENT '序号',  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态',  `qitemname` varchar(100) NOT NULL DEFAULT '' COMMENT '检验项目', `toleranceup` varchar(50) NOT NULL DEFAULT '' COMMENT '公差上限', `tolerancedown` varchar(50) NOT NULL DEFAULT '' COMMENT '公差下限', `device` varchar(100) NOT NULL DEFAULT '' COMMENT '检验方式', `qcontent` varchar(100) NOT NULL DEFAULT '' COMMENT '检验内容', `eligibility` varchar(100) NOT NULL DEFAULT '' COMMENT '合格标准', `testbasic` varchar(100) NOT NULL DEFAULT '' COMMENT '检验依据', `judgingcondition` int(1) NOT NULL DEFAULT '1' COMMENT '判断条件', `uplimit` varchar(50) NOT NULL DEFAULT '' COMMENT '上限值(合格值)',"
							+ "`downlimt` varchar(50) NOT NULL DEFAULT '' COMMENT '下限值',  `fvalue` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值', `fvalue1` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值1',  `fvalue2` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值2', `fvalue3` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值3',  `fvalue4` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值4', `fvalue5` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值5',  `fvalue6` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值6', `fvalue7` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值7', `fvalue8` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值8',"
							+ "`fvalue9` varchar(50) NOT NULL DEFAULT '' COMMENT '检测值9', `ispassed` int(1) NOT NULL DEFAULT '1' COMMENT '是否合格',  `checkdate` date DEFAULT NULL COMMENT '检测日期', `dremark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',  `dremark1` varchar(200) NOT NULL DEFAULT '' COMMENT '备注1',  `dremark2` varchar(200) NOT NULL DEFAULT '' COMMENT '备注2',  `dremark3` varchar(200) NOT NULL DEFAULT '' COMMENT '备注3',  PRIMARY KEY (`detailid`),  KEY `companyid` (`companyid`),  KEY `mainid` (`mainid`),  KEY `itemid` (`itemid`),  KEY `goods_number` (`goods_number`),  KEY `operate_time` (`operate_time`),  KEY `status` (`status`),  KEY `ispassed` (`ispassed`),  KEY `checkdate` (`checkdate`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质检单明细表'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `qualitybreakdetail` (  `detailid` varchar(36) NOT NULL COMMENT '编号',  `mainid` varchar(36) NOT NULL COMMENT '质检单ID',  `billtype` int(11) NOT NULL DEFAULT '1' COMMENT '质检类型',  `goods_number` int(11) NOT NULL DEFAULT '1' COMMENT '序号', `itemid` varchar(36) NOT NULL DEFAULT '' COMMENT '商品ID',  `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织编号',  `operate_time` date DEFAULT NULL COMMENT '单据日期',  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态',  `qualitytypeid` varchar(36) NOT NULL DEFAULT '' COMMENT '质量问题ID',"
							+ "`scount` double NOT NULL DEFAULT '0' COMMENT '不良数量', `processtype` int(1) NOT NULL DEFAULT '0' COMMENT '处理类型', `personlistid` varchar(500) NOT NULL DEFAULT '' COMMENT '责任人id', `personlistname` varchar(200) NOT NULL DEFAULT '' COMMENT '责任人', `reason` varchar(300) NOT NULL DEFAULT '' COMMENT '原因分析', `solution` varchar(300) NOT NULL DEFAULT '' COMMENT '解决措施',  `sremark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注', PRIMARY KEY (`detailid`),  KEY `companyid` (`companyid`),  KEY `billtype` (`billtype`),  KEY `status` (`status`),  KEY `mainid` (`mainid`),  KEY `qualitytypeid` (`qualitytypeid`),  KEY `operate_time` (`operate_time`),  KEY `itemid` (`itemid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质检单不良'");
					ps.addBatch("ALTER TABLE `iteminfo` CHANGE COLUMN `qualityremark` `qualityremark` VARCHAR(500) NULL DEFAULT '' COMMENT '质检说明' AFTER `quality`");

					ps.addBatch("ALTER TABLE `storeindetail` ADD COLUMN `isquality` INT(1) NULL DEFAULT '0' COMMENT '是否质检' AFTER `invoicemoney`,ADD COLUMN `isshowwarn` INT(1) NULL DEFAULT '0' COMMENT '质检提示' AFTER `isquality`, ADD INDEX `stype` (`stype`), ADD INDEX `operate_by` (`operate_by`), ADD INDEX `orderid` (`orderid`), ADD INDEX `isquality` (`isquality`),ADD INDEX `isshowwarn` (`isshowwarn`)");

					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `hasqualitymange` INT(1) NULL DEFAULT '0' AFTER `profession`");
					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `quality1` INT(1) NOT NULL DEFAULT '0' COMMENT '来料质检' AFTER `houselist`, ADD COLUMN `quality2` INT(1) NOT NULL DEFAULT '0' COMMENT '制程制检-首检' AFTER `quality1`, 	ADD COLUMN `quality3` INT(1) NOT NULL DEFAULT '0' COMMENT '制程制检-过程检' AFTER `quality2`, ADD COLUMN `quality4` INT(1) NOT NULL DEFAULT '0' COMMENT '制程制检-终检' AFTER `quality3`, ADD COLUMN `quality5` INT(1) NOT NULL DEFAULT '0' COMMENT '生产入库质检' AFTER `quality4`, 	ADD COLUMN `quality6` INT(1) NOT NULL DEFAULT '0' COMMENT '销售出库质检' AFTER `quality5`, ADD COLUMN `quality7` INT(1) NOT NULL DEFAULT '0' COMMENT '委外材料质检' AFTER `quality6`, ADD COLUMN `quality8` INT(1) NOT NULL DEFAULT '0' COMMENT '委外成品质检' AFTER `quality7`, ADD COLUMN `quality1begin` DATE NULL DEFAULT NULL COMMENT '来料质检开始日期' AFTER `quality8`, ADD COLUMN `quality2begin` DATE NULL DEFAULT NULL COMMENT '制程制检-首检开始日期' AFTER `quality1begin`, ADD COLUMN `quality3begin` DATE NULL DEFAULT NULL COMMENT '制程制检-过程检开始日期' AFTER `quality2begin`, 	ADD COLUMN `quality4begin` DATE NULL DEFAULT NULL COMMENT '制程制检-终检开始日期' AFTER `quality3begin`, ADD COLUMN `quality5begin` DATE NULL DEFAULT NULL COMMENT '生产入库质检开始日期' AFTER `quality4begin`, ADD COLUMN `quality6begin` DATE NULL DEFAULT NULL COMMENT '销售出库质检开始日期' AFTER `quality5begin`, ADD COLUMN `quality7begin` DATE NULL DEFAULT NULL COMMENT '委外材料质检开始日期' AFTER `quality6begin`, ADD COLUMN `quality8begin` DATE NULL DEFAULT NULL COMMENT '委外成品质检开始日期' AFTER `quality7begin`");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `morestaff` INT(1) NOT NULL DEFAULT '0' COMMENT '报工时可多选生产员工' AFTER `quality8begin`");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES"
							+ "('CB0EF6125250000193C71CE6121A1990', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 10, '新增', 'qualitieddata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-21 10:31:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:17:34', 1),"
							+ "('CB0EF61AA9000001D27D893010BC1DC1', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 11, '修改', 'qualitieddata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-21 10:32:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:19:34', 1),"
							+ "('CB0EF6445A90000176F98D40C5C013CA', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 17, '删除作废', 'qualitieddata:delete', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-21 10:35:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:31:11', 1),"
							+ "('CB14711B92700001396520831090C9B0', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 2, '查看来料质检', 'qualitieddata:show1', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:11:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:16:11', 1),"
							+ "('CB147129AA30000161AA149512C5146D', 4, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 3, '查看制程质检', 'qualitieddata:show2', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:12:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-12 15:19:03', 1),"
							+ "('CB1471391C600001468875F01B0D1147', 4, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 6, '查看生产入库质检', 'qualitieddata:show3', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:13:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-12 15:19:09', 1),"
							+ "('CB14713E7AE000013EF7153799201F21', 4, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 7, '查看销售出库质检', 'qualitieddata:show4', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:13:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-12 15:19:13', 1),"
							+ "('CB14714712D0000151D216501E308ED0', 4, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 8, '查看委外材料质检', 'qualitieddata:show5', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:14:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-12 15:19:17', 1),"
							+ "('CB14714D891000019CF0318868DD18D0', 4, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 9, '查看委外成品质检', 'qualitieddata:read6', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:14:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-12 15:19:23', 1),"
							+ "('CB147178C2F000015074EF44AF7ADD80', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 16, '作废', 'qualitieddata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:17:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:31:04', 1),"
							+ "('CB14718A94E00001C7A5106983751F3C', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 15, '审核', 'qualitieddata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:19:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:31:00', 1),"
							+ "('CB1471943FE00001F4731EA38673DBC0', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 13, '删除', 'qualitieddata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:19:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:30:55', 1),"
							+ "('CB1471AADFD00001FB9AB5BD1D30D500', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 12, '修改质检项', 'qualitieddata:changeitem', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:21:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:21:45', 1),"
							+ "('CB1472329CB00001E1121EB010F0125E', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 14, '详情', 'qualitieddata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:30:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:30:57', 1),"
							+ "('CB15B58C0BE00001B4BE13001A601018', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 19, '打印', 'qualitieddata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-11 09:41:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-11 09:42:25', 1),"
							+ "('CB17F535AD2000019DB4546010305440', 3, 1, 'App端', 'appdata', 11, '工序变更', 'stepchange', 1, 2, '修改单价/工时', 'stepchange:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-18 09:21:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-18 09:29:53', 1),"
							+ "('CB08D69FD1F00001ADBF85B0771013B6', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 1, '查看', 'qualitieddata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:58:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:58:31', 1),"
							+ "('CB08D6AAC120000145C1D0A01D001C60', 3, 29, '质量模块', 'qualitiedmodel', 20, '质检问题统计', 'qualitytjdata', 1, 1, '查看', 'qualitytjdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:59:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 09:59:16', 1),"
							+ "('CB08D77E0150000194401E801FD0D620', 3, 29, '质量模块', 'qualitiedmodel', 10, '质检单管理', 'qualitieddata', 1, 18, '导出数据', 'qualitieddata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:13:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-02-07 11:31:15', 1),"
							+ "('CB08D784B6300001395F55F0135C1863', 3, 29, '质量模块', 'qualitiedmodel', 20, '质检问题统计', 'qualitytjdata', 1, 1, '导出数据', 'qualitytjdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:14:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-01-02 10:14:20', 1)");

					ps.addBatch("ALTER TABLE `qualitybreakdetail` ADD COLUMN `qualitydetail` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '问题描述' AFTER `qualitytypeid`");

					ps.addBatch("ALTER TABLE `sysconfigure` ADD COLUMN `systemtype` INT(1) NOT NULL DEFAULT '0' COMMENT '系统类型' AFTER `fileuploadday`");

					ps.addBatch("ALTER TABLE `deliver` ADD INDEX `operate_time` (`operate_time`), ADD INDEX `operate_by` (`operate_by`)");
					ps.addBatch("ALTER TABLE `deliverdetail` ADD INDEX `operate_by` (`operate_by`), ADD INDEX `operate_time` (`operate_time`)");

					ps.addBatch("ALTER TABLE `t_order_progress` CHANGE COLUMN `user_id` `user_id` VARCHAR(200) NULL DEFAULT NULL COMMENT '生产员工' AFTER `workshop_id`");
					// ALTER TABLE `t_order_progress` ADD INDEX `user_id`
					// (`user_id`);

					// ps.addBatch("update t_order_progress ts set ts.user_id=ifnull((select st.staffid from staffinfo st where st.userid=ts.create_id limit 1),'') where user_id=''");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_orderprogress_staff` ( `id` varchar(50) NOT NULL COMMENT '主键',  `companyid` varchar(36) NOT NULL DEFAULT '' COMMENT '组织主键', `staffid` varchar(36) NOT NULL DEFAULT '' COMMENT '员工id',  `orderprogress_id` varchar(36) NOT NULL DEFAULT '' COMMENT 'progress_id',  PRIMARY KEY (`id`),  KEY `companyid` (`companyid`),  KEY `staffid` (`staffid`), KEY `orderprogress_id` (`orderprogress_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("INSERT INTO `t_orderprogress_staff` (`id`, `companyid`, `staffid`, `orderprogress_id`) select concat( id,'_',substring( user_id,length( user_id)-9)), companyid, user_id, id from t_order_progress where length(user_id)=32");

					ps.addBatch("ALTER TABLE `prodstoragedetail` CHANGE COLUMN `originalbill` `originalbill` VARCHAR(50) NULL DEFAULT '' COMMENT '原单号' AFTER `update_time`, CHANGE COLUMN `relationdetailid` `relationdetailid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联工单' AFTER `batchno`, CHANGE COLUMN `relationbillno` `relationbillno` VARCHAR(36) NULL DEFAULT '' COMMENT '关联工单编号' AFTER `relationdetailid`");

					ps.addBatch(" ALTER TABLE `accountbill` CHANGE COLUMN `honourremark` `honourremark` VARCHAR(200) NULL DEFAULT '' COMMENT '票据备注' AFTER `honourstatus`");
					ps.addBatch("update accountbill s set s.honourremark='' where s.honourremark='0'");

					ps.addBatch("INSERT INTO `accountmonth` (`monthid`, `companyid`, `accountid`, `sdate`, `syear`, `smonth`) select md5(replace(uuid(),'-','')+round(rand()*(1000-10)+10)),s.companyid,s.accountid,DATE_FORMAT(s.operate_time, '%Y-%m-01'),year(s.operate_time),month(s.operate_time) from accountbill s where s.bill_type in ('38','39') and s.operate_time is not null and s.`status`='1' and if((select 1 from accountmonth a where a.accountid=s.accountid and a.syear=year(s.operate_time) and a.smonth=month(s.operate_time) limit 1),false,true) group by s.accountid,year(s.operate_time),month(s.operate_time)");
					ps.addBatch("INSERT INTO `accountyear` (`yearid`, `companyid`, `accountid`, `syear`) select md5(replace(uuid(),'-','')+round(rand()*(1000-10)+10)),s.companyid,s.accountid,year(s.operate_time) from accountbill s where s.bill_type in ('38','39') and s.operate_time is not null and s.`status`='1' and if((select 1 from accountyear a where a.accountid=s.accountid and a.syear=year(s.operate_time)  limit 1),false,true) group by s.accountid,year(s.operate_time)");
					ps.addBatch("update accountmonth set money = 0,in_money=0,out_money=0 where 1=1");
					ps.addBatch("update accountyear set money = 0,in_money=0,out_money=0 where 1=1");
					ps.addBatch("update account set money = 0 where 1=1");

					ps.addBatch(" update accountmonth am left join (select f.companyid,f.accountid,f.syear,f.smonth,sum(f.rec_money) as in_money,sum(f.pay_money) as out_money  from (select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,if(ab.bill_type='38',ab.smoney,ab.rec_money) as rec_money,if(ab.bill_type='39',ab.smoney,ab.pay_money) as pay_money from accountbill ab where ab.status='1' and ab.operate_time is not null "
							+ " union all select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,ab.rec_money,ab.pay_money from dayinout ab where ab.status='1'"
							+ " union all select ab.companyid,ab.accountidout as accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,0 as rec_money, ab.smoney as pay_money from transfer ab where ab.status='1'"
							+ " union all select ab.companyid,ab.accountidin as accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,ab.smoney as rec_money, 0 as pay_money from transfer ab where ab.status='1'"
							+ ") f group by f.companyid,f.accountid,f.syear,f.smonth ) k on am.accountid=k.accountid and am.syear=k.syear and  am.smonth=k.smonth,s_company_config sc set am.in_money=round(ifnull(k.in_money,0),sc.moneybit),am.out_money=round(ifnull(k.out_money,0),sc.moneybit),am.money=round(ifnull(k.in_money,0)-ifnull(k.out_money,0),sc.moneybit)  where  am.companyid=sc.company_id ");

					ps.addBatch("update accountyear am  left join (select f.companyid,f.accountid,f.syear,sum(f.rec_money) as in_money,sum(f.pay_money) as out_money  from (select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,if(ab.bill_type='38',ab.smoney,ab.rec_money) as rec_money,if(ab.bill_type='39',ab.smoney,ab.pay_money) as pay_money from accountbill ab where ab.status='1' and ab.operate_time is not null  union all select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,ab.rec_money,ab.pay_money from dayinout ab where ab.status='1'  union all select ab.companyid,ab.accountidout as accountid,year(ab.operate_time) as syear ,0 as rec_money, ab.smoney as pay_money from transfer ab where ab.status='1' union all select ab.companyid,ab.accountidin as accountid,year(ab.operate_time) as syear ,ab.smoney as rec_money, 0 as pay_money from transfer ab where ab.status='1' ) f  group by f.companyid,f.accountid,f.syear ) k on am.accountid=k.accountid and  am.syear=k.syear,s_company_config sc set am.in_money=round(ifnull"
							+ "(k.in_money,0),sc.moneybit),am.out_money=round(ifnull(k.out_money,0),sc.moneybit),am.money=round(ifnull(k.in_money,0)-ifnull(k.out_money,0),sc.moneybit)  where am.companyid=sc.company_id");

					ps.addBatch("update account s,s_company_config sc set s.money=round(s.beginmoney+ifnull((select sum(am.money) from accountmonth am where am.accountid=s.accountid),0),sc.moneybit) where s.companyid=sc.company_id");

					// 2.7转2.6
					ps.addBatch("ALTER TABLE `t_progress` ADD INDEX `finish_time` (`finish_time`)");
					ps.addBatch("ALTER TABLE `iteminfo` ADD COLUMN `hasfile` INT(1) NULL DEFAULT '0' COMMENT '是否有附件' AFTER `qupdate_time`");
					ps.addBatch("update iteminfo s set s.hasfile = ifnull((select 1 from t_file t where t.detail_id=s.itemid and t.fstatus=3 limit 1),0)   where  1=1");
					ps.addBatch("ALTER TABLE `uploadimage` 	ADD INDEX `create_date` (`create_date`)");
					ps.addBatch("update t_order t ,t_order_detail td set td.batchno=t.batchno where t.id=td.order_id");
				}

				if (version < 2.7 && newversion >= 2.7) {
					ps.addBatch("ALTER TABLE `purchaseorderdetail` ADD COLUMN `relationtype` INT(1) NOT NULL DEFAULT '0' COMMENT '关联类型' AFTER `relationmainid`");
					ps.addBatch("ALTER TABLE `t_order` ADD COLUMN `relationdetailid` VARCHAR(50) NULL DEFAULT '' COMMENT '关联补料申请' AFTER `canincount`, ADD COLUMN `relationorderid` VARCHAR(50) NULL DEFAULT '' COMMENT '关联补料单号' AFTER `relationdetailid`, ADD COLUMN `relationmainid` VARCHAR(50) NULL DEFAULT '' COMMENT '关联补料主表' AFTER `relationorderid`, ADD INDEX `relationdetailid` (`relationdetailid`), ADD INDEX `relationmainid` (`relationmainid`)");

					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `hasapplymodel` INT(1) NULL DEFAULT '0' AFTER `hasqualitymange`");

					// ps.addBatch("ALTER TABLE `t_order_detail`  DROP INDEX `companyid_billno`");//DROP
					// INDEX `companyid_id`,

					ps.addBatch("ALTER TABLE `staffinfo` ADD INDEX `staffcode` (`staffcode`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_config` ( `companyid` varchar(36) DEFAULT NULL COMMENT 'ID',  `customer_name` varchar(500) DEFAULT '' COMMENT '申请部门',  `teams` varchar(500) DEFAULT '' COMMENT '申请班组',  `payproperty` varchar(100) DEFAULT '' COMMENT '款项性质',  `paytype` varchar(100) DEFAULT '' COMMENT '付款方式', `currency` varchar(200) DEFAULT '' COMMENT '款币种',"
							+ "`payproject` varchar(500) DEFAULT '' COMMENT '支付项目',  `invoicetype` varchar(200) DEFAULT '' COMMENT '开票申请类别', `leavetype` varchar(100) DEFAULT '' COMMENT '请假类别',  `overtimetype` varchar(500) DEFAULT '' COMMENT '加班事由',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) DEFAULT '' COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间',  UNIQUE KEY `companyid` (`companyid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_invoice` (  `mainid` varchar(36) NOT NULL DEFAULT '' COMMENT 'ID', `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型', `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `originalbill` varchar(50) DEFAULT '' COMMENT '原单号',  `orderid` varchar(36) DEFAULT '' COMMENT '申请单号',  `operate_time` date DEFAULT NULL COMMENT '申请日期', `operate_by` varchar(36) DEFAULT '' COMMENT '申请人',"
							+ " `customerid` varchar(36) DEFAULT '' COMMENT '客户ID',  `customertype` varchar(50) DEFAULT '' COMMENT '类别',  `count` double DEFAULT '0' COMMENT '数量',  `totaltax` double DEFAULT '0' COMMENT '总税额', `totalmoney` double DEFAULT '0' COMMENT '价税总额', `total` double DEFAULT '0' COMMENT '人民币总额', `currency` varchar(10) DEFAULT '' COMMENT '币种',  `remark` varchar(200) DEFAULT '' COMMENT '备注',  `status` varchar(1) DEFAULT '0' COMMENT '状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',"
							+ "`create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人', `update_by` varchar(50) DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID', `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`mainid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `operate_by` (`operate_by`),  KEY `customertype` (`customertype`),  KEY `status` (`status`),  KEY `create_time` (`create_time`),  KEY `audit_time` (`audit_time`),  KEY `update_time` (`update_time`),  KEY `create_id` (`create_id`),  KEY `orderid` (`orderid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_invoicedetail` (  `detailid` varchar(36) NOT NULL DEFAULT '' COMMENT '编号',   `mainid` varchar(36) DEFAULT '' COMMENT '申请ID',   `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',   `goods_number` int(11) DEFAULT '0' COMMENT '序号',   `invoicedate` date DEFAULT NULL COMMENT '开票日期',  `receivedate` date DEFAULT NULL COMMENT '预计到款日期',  `itemname` varchar(100) DEFAULT '' COMMENT '商品名称',  `itemsformat` varchar(100) DEFAULT '' COMMENT '商品规格',   `itemunit` varchar(20) DEFAULT '' COMMENT '单位',"
							+ " `scount` double DEFAULT '0' COMMENT '数量',   `price` double DEFAULT '0' COMMENT '不含税单价',   `currency` varchar(10) DEFAULT '' COMMENT '币种',   `exrate` double DEFAULT '0' COMMENT '汇率',   `rmbprice` double DEFAULT '0' COMMENT '人民币不含税单价',  `stotal` double DEFAULT '0' COMMENT '人民币金额',   `taxrate` double DEFAULT '0' COMMENT '税率(%)',   `tax` double DEFAULT '0' COMMENT '税额',  `taxprice` double DEFAULT '0' COMMENT '含税单价',   `taxmoney` double DEFAULT '0' COMMENT '价税合计',   `remark` varchar(200) DEFAULT '' COMMENT '备注',   `status` varchar(1) DEFAULT '0' COMMENT '状态',   `mupdate_id` varchar(36) DEFAULT '' COMMENT '更新人ID',   `mupdate_by` varchar(50) DEFAULT '' COMMENT '更新人',   `mupdate_time` datetime DEFAULT NULL COMMENT '更新时间',   PRIMARY KEY (`detailid`),   KEY `mainid` (`mainid`),   KEY `companyid` (`companyid`),   KEY `invoicedate` (`invoicedate`),   KEY `receivedate` (`receivedate`),   KEY `goods_number` (`goods_number`),   KEY `status` (`status`) 	) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_iteminfo` ( `mainid` varchar(36) NOT NULL DEFAULT '' COMMENT 'ID',  `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型', `companyid` varchar(36) DEFAULT '' COMMENT '企业编号', `orderid` varchar(36) DEFAULT '' COMMENT '申请单号',  `operate_time` date DEFAULT NULL COMMENT '申请日期',"
							+ "`operate_by` varchar(36) DEFAULT '' COMMENT '申请人', `applytype` int(1) DEFAULT '0' COMMENT '申请内容',  `remark` varchar(200) DEFAULT '' COMMENT '备注',  `status` varchar(1) DEFAULT '0' COMMENT '状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人',  `update_by` varchar(50) DEFAULT '' COMMENT '更新人',"
							+ "`update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`mainid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `operate_by` (`operate_by`),  KEY `applytype` (`applytype`),  KEY `status` (`status`),  KEY `create_time` (`create_time`),  KEY `audit_time` (`audit_time`),  KEY `update_time` (`update_time`),  KEY `create_id` (`create_id`),  KEY `orderid` (`orderid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_iteminfodetail` (  `detailid` varchar(36) NOT NULL DEFAULT '' COMMENT '编号',   `mainid` varchar(36) DEFAULT '' COMMENT '申请ID', `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `goods_number` int(11) DEFAULT '0' COMMENT '序号',  `applytype` int(1) DEFAULT '0' COMMENT '申请内容', "
							+ "`itemid` varchar(36) DEFAULT '' COMMENT '商品ID',  `customerid` varchar(36) DEFAULT '' COMMENT '往来单位ID',  `customername` varchar(100) DEFAULT '' COMMENT '往来单位',  `changeitem` varchar(20) DEFAULT '' COMMENT '变更商品项', `changeitemid` varchar(30) DEFAULT '' COMMENT '变更商品项id', `oldinfo` varchar(200) DEFAULT '' COMMENT '变更前',  `newinfo` varchar(200) DEFAULT '' COMMENT '变更后',"
							+ "`codeid` varchar(50) DEFAULT '' COMMENT '商品编号', `itemname` varchar(100) DEFAULT '' COMMENT '商品名称',  `sformat` varchar(100) DEFAULT '' COMMENT '商品规格', `classid` varchar(36) DEFAULT '' COMMENT '商品分类ID',  `classname` varchar(50) DEFAULT '' COMMENT '商品分类',  `unit` varchar(50) DEFAULT '' COMMENT '单位',  `inprice` double DEFAULT '0' COMMENT '进货单价',  `outprice` double DEFAULT '0' COMMENT '零售单价',  `outsourcingprice` double DEFAULT '0' COMMENT '委外加工单价', `barcode` varchar(100) DEFAULT '' COMMENT '商品码',  `property1` varchar(100) DEFAULT '' COMMENT '属性1',   `property2` varchar(100) DEFAULT '' COMMENT '属性2',  `property3` varchar(100) DEFAULT '' COMMENT '属性3',  `property4` varchar(100) DEFAULT '' COMMENT '属性4',  `property5` varchar(100) DEFAULT '' COMMENT '属性5',  `class_id` varchar(36) DEFAULT '' COMMENT '生产工艺ID',  `class_name` varchar(50) DEFAULT '' COMMENT '生产工艺',  `itemremark` varchar(200) DEFAULT '' COMMENT '商品备注',  `remark` varchar(200) DEFAULT '' COMMENT '申请备注',"
							+ "`status` varchar(1) DEFAULT '0' COMMENT '状态',  `mupdate_id` varchar(36) DEFAULT '' COMMENT '更新人ID',  `mupdate_by` varchar(50) DEFAULT '' COMMENT '更新人',  `mupdate_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`detailid`),  KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`),  KEY `applytype` (`applytype`),   KEY `goods_number` (`goods_number`),   KEY `status` (`status`),  KEY `customername` (`customername`), KEY `itemname` (`itemname`),  KEY `classname` (`classname`),  KEY `class_name` (`class_name`),  KEY `changeitemid` (`changeitemid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_leave` (  `mainid` varchar(36) NOT NULL DEFAULT '' COMMENT 'ID',  `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `operate_time` date DEFAULT NULL COMMENT '申请日期',  `operate_by` varchar(36) DEFAULT '' COMMENT '申请人',  `customername` varchar(50) DEFAULT '' COMMENT '申请部门',"
							+ "`teamname` varchar(50) DEFAULT '' COMMENT '申请班组',  `orderid` varchar(50) DEFAULT '' COMMENT '单据编号', `leavetype` varchar(10) DEFAULT '' COMMENT '请假类型', `datetype` int(1) DEFAULT '0' COMMENT '时间类型', `begindate` date DEFAULT NULL COMMENT '开始时间', `enddate` date DEFAULT NULL COMMENT '结束时间',  `begintime` time DEFAULT NULL COMMENT '开始时间',"
							+ "`endtime` time DEFAULT NULL COMMENT '结束时间',  `sbegin` int(1) DEFAULT '0' COMMENT '开始上下午', `send` int(1) DEFAULT '0' COMMENT '结束上下午', `leavetime` double DEFAULT '0' COMMENT '请假时长',  `reason` varchar(200) DEFAULT '' COMMENT '请假事由',  `status` varchar(1) DEFAULT '0' COMMENT '状态', `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人', `update_by` varchar(50) DEFAULT '' COMMENT '更新人',"
							+ "`update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`mainid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `leavetype` (`leavetype`),  KEY `begindate` (`begindate`),  KEY `enddate` (`enddate`),  KEY `customername` (`customername`),  KEY `teamname` (`teamname`),  KEY `status` (`status`),  KEY `create_time` (`create_time`),  KEY `audit_time` (`audit_time`),  KEY `update_time` (`update_time`),  KEY `create_id` (`create_id`),  KEY `orderid` (`orderid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_leavedetail` (  `detailid` varchar(36) NOT NULL DEFAULT '' COMMENT '编号',  `mainid` varchar(36) DEFAULT '' COMMENT '申请ID',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `goods_number` int(11) DEFAULT '0' COMMENT '序号',  `leavedate` date DEFAULT NULL COMMENT '假单日期',  `leavetime` double DEFAULT '0' COMMENT '请假时长', `status` varchar(1) DEFAULT '0' COMMENT '状态',  `mupdate_id` varchar(36) DEFAULT '' COMMENT '更新人ID',  `mupdate_by` varchar(50) DEFAULT '' COMMENT '更新人',  `mupdate_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`detailid`),   KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`),  KEY `leavedate` (`leavedate`),  KEY `status` (`status`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_material` (  `mainid` varchar(36) NOT NULL DEFAULT '' COMMENT 'ID',  `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型', `originalbill` varchar(50) DEFAULT '' COMMENT '原单号', `materialtype` int(1) DEFAULT '0' COMMENT '补料类型',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `operate_time` date DEFAULT NULL COMMENT '申请日期',  `operate_by` varchar(36) DEFAULT '' COMMENT '申请人',  `customerid` varchar(36) DEFAULT '' COMMENT '申请部门',  `orderid` varchar(50) DEFAULT '' COMMENT '单据编号',  `maincount` double DEFAULT '0' COMMENT '产品数量',  `detailcount` double DEFAULT '0' COMMENT '材料数量',  `remark` varchar(200) DEFAULT '' COMMENT '备注',  `status` varchar(1) DEFAULT '0' COMMENT '状态', `processstatus1` int(1) DEFAULT '0' COMMENT '采购状态',  `processstatus2` int(1) DEFAULT '0' COMMENT '排产状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人',  `update_by` varchar(50) DEFAULT '' COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间', `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`mainid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `operate_by` (`operate_by`),  KEY `customerid` (`customerid`),  KEY `processstatus1` (`processstatus1`),  KEY `status` (`status`),  KEY `processstatus2` (`processstatus2`),  KEY `create_time` (`create_time`),  KEY `audit_time` (`audit_time`),  KEY `update_time` (`update_time`),  KEY `create_id` (`create_id`),  KEY `materialtype` (`materialtype`),  KEY `orderid` (`orderid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_materialdetail` (  `detailid` varchar(36) NOT NULL DEFAULT '' COMMENT '编号',  `mainid` varchar(36) DEFAULT '' COMMENT '补料申请ID',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `plandate` date DEFAULT NULL COMMENT '期望到货日期',  `itemid` varchar(36) DEFAULT '' COMMENT '商品ID',  `fcustomerid` varchar(36) DEFAULT '' COMMENT '客户ID',"
							+ " `goods_number` int(11) DEFAULT '0' COMMENT '序号',  `sno` int(11) DEFAULT '0' COMMENT '排序号',  `proccesstype` varchar(100) DEFAULT '' COMMENT '不良处理说明', `count` double DEFAULT '0' COMMENT '数量', `dcount` double DEFAULT '0' COMMENT '材料数量',  `remark` varchar(200) DEFAULT '' COMMENT '备注', `stype` int(11) DEFAULT '0' COMMENT '类型', `status` varchar(1) DEFAULT '0' COMMENT '状态',  `processstatus` int(1) DEFAULT '0' COMMENT '流程状态', `mupdate_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `mupdate_by` varchar(50) DEFAULT '' COMMENT '更新人',  `mupdate_time` datetime DEFAULT NULL COMMENT '更新时间',  `relationdetailid` varchar(36) DEFAULT '' COMMENT '关联单明细ID',  `relationorderid` varchar(36) DEFAULT '' COMMENT '关联单单号',  `relationmainid` varchar(36) DEFAULT '' COMMENT '关联单主表id',"
							+ "`parentid` varchar(36) DEFAULT '' COMMENT '父ID',   `order_id` varchar(36) NOT NULL DEFAULT '' COMMENT '工单ID',  `billno` varchar(36) DEFAULT '' COMMENT '工单号',  `scheduleid` varchar(36) DEFAULT '' COMMENT '排产ID',  PRIMARY KEY (`detailid`),  KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`),  KEY `pandate` (`plandate`),  KEY `itemid` (`itemid`),  KEY `fcustomerid` (`fcustomerid`),  KEY `stype` (`stype`),  KEY `processstatus` (`processstatus`),  KEY `relationdetailid` (`relationdetailid`),  KEY `relationorderid` (`relationorderid`),  KEY `parentid` (`parentid`),  KEY `relationmainid` (`relationmainid`),  KEY `order_id` (`order_id`),  KEY `scheduleid` (`scheduleid`),  KEY `sno` (`sno`),  KEY `status` (`status`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_overtime` (  `mainid` varchar(36) NOT NULL DEFAULT '' COMMENT 'ID',  `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `operate_time` date DEFAULT NULL COMMENT '申请日期',  `operate_by` varchar(36) DEFAULT '' COMMENT '申请人',  `customername` varchar(50) DEFAULT '' COMMENT '申请部门',  `teamname` varchar(50) DEFAULT '' COMMENT '申请班组',  `orderid` varchar(50) DEFAULT '' COMMENT '单据编号',  `customer` varchar(50) DEFAULT '' COMMENT '对应客户',  `overtimetype` int(1) DEFAULT '0' COMMENT '加班类型',  `begindate` date DEFAULT NULL COMMENT '开始时间',"
							+ " `enddate` date DEFAULT NULL COMMENT '结束时间',  `begintime` time DEFAULT NULL COMMENT '开始时间',  `endtime` time DEFAULT NULL COMMENT '结束时间',  `leavetime` double DEFAULT '0' COMMENT '加班时长(小时)',  `overtime` double DEFAULT '0' COMMENT '加班时长(分钟)',  `overtimereason` varchar(30) DEFAULT '' COMMENT '加班事由',  `reason` varchar(200) DEFAULT '' COMMENT '具体事由',  `status` varchar(1) DEFAULT '0' COMMENT '状态',   `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人',  `update_by` varchar(50) DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`mainid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `overtimetype` (`overtimetype`),  KEY `begindate` (`begindate`),  KEY `enddate` (`enddate`),  KEY `customername` (`customername`),  KEY `teamname` (`teamname`),  KEY `status` (`status`),   KEY `create_time` (`create_time`),  KEY `audit_time` (`audit_time`),  KEY `update_time` (`update_time`),  KEY `create_id` (`create_id`),  KEY `orderid` (`orderid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_payment` (   `id` varchar(36) NOT NULL DEFAULT '' COMMENT 'ID',  `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型',  `originalbill` varchar(50) DEFAULT '' COMMENT '原单号',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `operate_time` date DEFAULT NULL COMMENT '申请日期',  `plandate` date DEFAULT NULL COMMENT '期望支付日期',  `operate_by` varchar(36) DEFAULT '' COMMENT '申请人', `customer_name` varchar(50) DEFAULT '' COMMENT '申请部门',  `orderid` varchar(50) DEFAULT '' COMMENT '单据编号',  `contractno` varchar(50) DEFAULT '' COMMENT '合同号',  `payproperty` varchar(20) DEFAULT '' COMMENT '款项性质',  `payee` varchar(80) DEFAULT '' COMMENT '收款单位/个人',  `paytype` varchar(20) DEFAULT '' COMMENT '付款方式', `payproject` varchar(20) DEFAULT '' COMMENT '支付项目',  `currency` varchar(10) DEFAULT '' COMMENT '付款币种', `paymoney` double DEFAULT '0' COMMENT '付款金额', `payremark` varchar(200) DEFAULT '' COMMENT '支付说明',"
							+ "`paynumber` varchar(50) DEFAULT '' COMMENT '财务编号',  `paydate` date DEFAULT NULL COMMENT '支付日期',  `exrate` double DEFAULT '0' COMMENT '当日汇率',  `actmoney` double DEFAULT '0' COMMENT '人民币金额',  `remark` varchar(200) DEFAULT '' COMMENT '备注',  `status` varchar(1) DEFAULT '0' COMMENT '状态',  `paystatus` int(1) DEFAULT '0' COMMENT '付款状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',   `outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) DEFAULT '' COMMENT '创建人',"
							+ " `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人',  `update_by` varchar(50) DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  PRIMARY KEY (`id`),  KEY `companyid` (`companyid`),   KEY `operate_time` (`operate_time`),  KEY `paydate` (`paydate`),  KEY `operate_by` (`operate_by`),  KEY `payee` (`payee`),  KEY `paystatus` (`paystatus`),  KEY `status` (`status`),  KEY `create_time` (`create_time`),  KEY `audit_time` (`audit_time`),  KEY `update_time` (`update_time`),  KEY `create_id` (`create_id`),  KEY `orderid` (`orderid`),KEY `contractno` (`contractno`),  KEY `plandate` (`plandate`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `purchasetype` VARCHAR(500) NULL DEFAULT '' COMMENT '采购申请类别' AFTER `morestaff`");
					ps.addBatch("ALTER TABLE `purchase` ADD COLUMN `purchasetype` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '采购类别' AFTER `remark`, ADD INDEX `purchasetype` (`purchasetype`)");
					ps.addBatch("ALTER TABLE `purchasedetail` ADD COLUMN `purchasetype` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '采购类型' AFTER `originalbill`, ADD INDEX `originalbill` (`originalbill`), ADD INDEX `purchasetype` (`purchasetype`)");
					ps.addBatch("ALTER TABLE `purchase` ADD INDEX `originalbill` (`originalbill`)");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES"
							+ " ('CB372C548AB00001F9C8D2F5E87058E0', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 1, '查看', 'applymaterialdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 08:56:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:59', 2),"
							+ "('CB372C65BD700001CCAF184372511392', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 16, '主附件上传', 'applymaterialdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 08:58:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:08:34', 1),"
							+ "('CB372C6DAB0000019DDB1A17B2901B2F', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 2, '新增', 'applymaterialdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 08:58:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:17', 1),"
							+ "('CB372C92117000012BBEE1B011C31717', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 3, '修改', 'applymaterialdata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:01:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:15', 1),"
							+ "('CB372CA0F2F0000181F31DBC834B194D', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 4, '复制', 'applymaterialdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:02:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:02:04', 1),"
							+ "('CB372CA6C3C0000119D8100015507500', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 5, '详情', 'applymaterialdata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:02:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:02:28', 1),"
							+ "('CB372CAB617000013F30D4504CDA4970', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 6, '作废', 'applymaterialdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:02:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:02:47', 1),"
							+ "('CB372CB5C3E0000144A934551C401AE0', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 7, '删除', 'applymaterialdata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:03:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:03:29', 1),"
							+ "('CB372CBE1F6000015E5F1A1C10485EA0', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 8, '审核', 'applymaterialdata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:04:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:04:04', 1),"
							+ "('CB372CC41940000161D41D20BCA67640', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 9, '修改采购状态', 'applymaterialdata:processstatus1', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:04:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:04:28', 1),"
							+ "('CB372CC96BF000016BE510401DEF1742', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 11, '修改排产状态', 'applymaterialdata:processstatus2', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:04:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:04:50', 1),"
							+ "('CB372CDC6A60000153859C802DA01CE9', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 13, '修改详情信息', 'applymaterialdata:modifyinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:06:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:06:08', 1),"
							+ "('CB372CEFB2E00001902F1A0AD1507950', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 14, '导出数据', 'applymaterialdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:07:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:07:27', 1),"
							+ "('CB372CF3E8500001767186D081B07490', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 15, '打印', 'applymaterialdata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:07:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:07:44', 1),"
							+ "('CB372CFB7DE0000110A816409F101388', 3, 10, '申请模块', 'applymodel', 10, '补料申请管理', 'applymaterialdata', 1, 17, '主附件查看', 'applymaterialdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:08:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:21:47', 1),"
							+ "('CB372D60D0A00001F8521B0E1F801C39', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 13, '主附件查看', 'applypaymentdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:15:10', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:15', 1),"
							+ "('CB372D676940000168FD1CA073451BC5', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 1, '查看', 'applypaymentdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:15:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:57', 2),"
							+ "('CB372D6DFD300001E9424EC017B513EB', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 2, '新增', 'applypaymentdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:16:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:54', 1),"
							+ "('CB372D7EA8800001CBBC13C01BB015BA', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 3, '修改', 'applypaymentdata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:17:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:49', 1),"
							+ "('CB372D83C1F00001CA3C194CA720D3C0', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 4, '复制', 'applypaymentdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:17:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:45', 1),"
							+ "('CB372D89156000015BFE1E2928401867', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 5, '详情', 'applypaymentdata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:17:55', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:42', 1),"
							+ "('CB372D8D398000017F47150016908250', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 6, '作废', 'applypaymentdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:18:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:39', 1),"
							+ "('CB372D930A5000019F4EBB507B701283', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 7, '删除', 'applypaymentdata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:18:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:35', 1),"
							+ "('CB372D9A4E9000019B671DC01B0669E0', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 8, '审核', 'applypaymentdata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:19:05', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:30', 1),"
							+ "('CB372DAD2D000001DC2810344A801256', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 9, '修改支付状态', 'applypaymentdata:paystatus', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:20:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:26', 1),"
							+ "('CB372DB177500001D7AFC3AE3B1E5220', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 10, '导出数据', 'applypaymentdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:20:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:23', 1),"
							+ "('CB372DB6C890000115604B70DA501955', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 12, '打印', 'applypaymentdata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:21:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:17', 1),"
							+ "('CB372DBCDE000001D72A1DC084601329', 3, 10, '申请模块', 'applymodel', 20, '付款申请管理', 'applypaymentdata', 1, 14, '主附件上传', 'applypaymentdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:21:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:12', 1),"
							+ "('CB372DD2DE500001851A1E9092801836', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 1, '查看', 'applyinvoicedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:22:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:08', 2),"
							+ "('CB372DDBC1E00001583813A01DBA1BA0', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 2, '新增', 'applyinvoicedata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:23:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:04', 1),"
							+ "('CB372DDFF6D00001658A262C99001B9E', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 3, '修改', 'applyinvoicedata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:23:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:14:01', 1),"
							+ "('CB372DE3C4D000011D2663821F53AC40', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 4, '复制', 'applyinvoicedata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:24:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:55', 1),"
							+ "('CB372DEC694000019352ED4D1144122E', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 5, '详情', 'applyinvoicedata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:24:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:53', 1),"
							+ "('CB372DF122200001B8451BC01370BB80', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 6, '作废', 'applyinvoicedata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:25:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:49', 1),"
							+ "('CB372DF7EB800001276C12D0299E9050', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 7, '删除', 'applyinvoicedata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:25:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:46', 1),"
							+ "('CB372E0331500001D84B656E1CE01657', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 8, '审核', 'applyinvoicedata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:26:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:40', 1),"
							+ "('CB372E086120000129A21C3F19CA1703', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 9, '导出数据', 'applyinvoicedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:26:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:38', 1),"
							+ "('CB372E0DB5D000012D5C7BD047D9C190', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 10, '打印', 'applyinvoicedata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:26:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:34', 1),"
							+ "('CB372E0DDEE00001C93D17001029195F', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 11, '主附件上传', 'applyinvoicedata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:26:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:29', 1),"
							+ "('CB372E1916900001D79C15B4AD779BD0', 3, 10, '申请模块', 'applymodel', 30, '开票申请管理', 'applyinvoicedata', 1, 12, '主附件查看', 'applyinvoicedata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 09:27:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:13:26', 1),"
							+ "('CB3730D20F900001CA781840D6805DA0', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 1, '查看', 'applyleavedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:15:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:16:04', 2),"
							+ "('CB3730DD28700001EFA112081D904A10', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 2, '新增', 'applyleavedata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:16:05', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:16:18', 1),"
							+ "('CB3730E0A4F0000182D51AB41151BEB0', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 3, '修改', 'applyleavedata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:16:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:16:39', 1),"
							+ "('CB3730E5A310000147476740A3A0167A', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 4, '复制', 'applyleavedata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:16:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:17:01', 1),"
							+ "('CB3730EB156000012AFAC5401FE012FD', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 5, '详情', 'applyleavedata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:17:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:17:18', 1),"
							+ "('CB3730EF1D500001C5FA1FB5E04D1BC5', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 6, '作废', 'applyleavedata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:17:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:17:49', 1),"
							+ "('CB3730F6FE9000013CFEA8F019751204', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 7, '删除', 'applyleavedata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:17:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:18:35', 1),"
							+ "('CB3730FCF380000139CF13581B00D930', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 8, '审核', 'applyleavedata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:18:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:18:40', 1),"
							+ "('CB37310348F00001E0BE1B521A0027F0', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 9, '导出数据', 'applyleavedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:18:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:19:08', 1),"
							+ "('CB37310A10900001B9644400AC651FA0', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 10, '打印', 'applyleavedata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:19:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:19:52', 1),"
							+ "('CB37311377F000017FE7697D58409380', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 11, '主附件上传', 'applyleavedata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:19:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:20:37', 1),"
							+ "('CB373113AFD00001A9B02E90D0A01643', 3, 10, '申请模块', 'applymodel', 40, '请假申请管理', 'applyleavedata', 1, 12, '主附件查看', 'applyleavedata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:19:48', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:20:48', 1),"
							+ "('CB373132EB900001B6BA1977182818DF', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 1, '查看', 'applyovertimedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:21:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:22:23', 2),"
							+ "('CB37313A5F6000011D1A16703139EF60', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 2, '新增', 'applyovertimedata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:22:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:19', 1),"
							+ "('CB37313D4040000194ECDAF0195013D8', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 3, '修改', 'applyovertimedata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:22:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:20', 1),"
							+ "('CB37314070D00001ED76778B1B801357', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 4, '复制', 'applyovertimedata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:22:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:21', 1),"
							+ "('CB373143BCD00001AB9894781B3019EB', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 5, '详情', 'applyovertimedata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:23:05', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:22', 1),"
							+ "('CB3731475AD00001E98A17A01AE01B0F', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 6, '作废', 'applyovertimedata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:23:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:26', 1),"
							+ "('CB37314CD4400001DA141400BF901A20', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 7, '删除', 'applyovertimedata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:23:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:28', 1),"
							+ "('CB3731507440000110B767917B30E7F0', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 8, '审核', 'applyovertimedata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:23:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:29', 1),"
							+ "('CB3731596F7000015581FF807DDB2AB0', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 9, '导出数据', 'applyovertimedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:24:34', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:25:04', 1),"
							+ "('CB373160D4F00001B2FE9FF0FF90130A', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 11, '主附件上传', 'applyovertimedata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:25:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-02 14:36:46', 1),"
							+ "('CB3731694E300001753413C6ECD08A50', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 12, '主附件查看', 'applyovertimedata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:25:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-02 14:36:50', 1),"
							+ "('CB37318422500001938316201DEE18CC', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 1, '查看', 'applyiteminfodata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:27:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:54:30', 2),"
							+ "('CB37318D08F00001C324150011F518EE', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 2, '新增', 'applyiteminfodata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:28:05', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:54:33', 1),"
							+ "('CB3731AC71A000012624120916801B11', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 3, '修改', 'applyiteminfodata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:30:14', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:54:36', 1),"
							+ "('CB3731D5ED700001F41C1EBD12EBDA70', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 5, '详情', 'applyiteminfodata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:33:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:54:45', 1),"
							+ "('CB3731E0F8F0000128462C10136ED4E0', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 10, '打印', 'applyiteminfodata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:33:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:55:05', 1),"
							+ "('CB373204C5E0000171A31445D3F09DA0', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 7, '删除', 'applyiteminfodata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:36:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:54:53', 1),"
							+ "('CB37320781500001334D650031B087E0', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 8, '审核', 'applyiteminfodata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:36:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:54:57', 1),"
							+ "('CB37320E79D00001DAD7B94A29741E29', 3, 10, '申请模块', 'applymodel', 60, '商品信息申请管理', 'applyiteminfodata', 1, 9, '导出数据', 'applyiteminfodata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:36:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:55:02', 1),"
							+ "('CB37323C07F00001AA90103C7381156D', 3, 10, '申请模块', 'applymodel', 90, '申请模块配置', 'applysetdata', 1, 1, '查看', 'applysetdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:40:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:42:26', 1),"
							+ "('CB373257D9300001C795BB29145415BE', 3, 10, '申请模块', 'applymodel', 90, '申请模块配置', 'applysetdata', 1, 2, '保存', 'applysetdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:41:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:42:36', 1),"
							+ "('CB373262FE3000019B5A31001CF2FEF0', 3, 10, '申请模块', 'applymodel', 100, '请假加班统计查询', 'applytjdata', 1, 1, '查看', 'applytjdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:42:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:43:53', 1),"
							+ "('CB37326996100001359F6F10FD341581', 3, 10, '申请模块', 'applymodel', 100, '请假加班统计查询', 'applytjdata', 1, 2, '导出数据', 'applytjdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:43:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-26 10:43:50', 1),"
							+ "('CB38892CCE7000019E3C7201F9E47150', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 16, '主附件查看', 'purchasedata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-30 14:33:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-02 14:46:33', 1),"
							+ "('CB38892D45B00001674F10CE1FDF19F6', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 15, '主附件上传', 'purchasedata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-05-30 14:33:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-02 14:46:30', 1),"
							+ "('CB398084B07000013CF5CB7B3290E580', 3, 10, '申请模块', 'applymodel', 50, '加班申请管理', 'applyovertimedata', 1, 10, '打印', 'applyovertimedata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-02 14:36:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-02 14:36:42', 1),"
							+ "('CB3ACD77880000018931751651531FDF', 3, 20, '销售模块', 'storeoutmodel', 50, '销售订单利润评估', 'orderprofitdata', 2, 1, '查看', 'orderprofitdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-06 15:34:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-06 15:35:13', 1),"
							+ "('CB3ACD8510500001F5341BE02910AA20', 3, 20, '销售模块', 'storeoutmodel', 50, '销售订单利润评估', 'orderprofitdata', 2, 2, '修改费用备注', 'orderprofitdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-06 15:35:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-06 15:35:57', 1),"
							+ "('CB3ACD8A65700001AE911640AA5B10DB', 3, 20, '销售模块', 'storeoutmodel', 50, '销售订单利润评估', 'orderprofitdata', 2, 3, '导出数据', 'orderprofitdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-06 15:35:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-06 15:36:13', 1),"
							+ "('CB3F0EEE5B20000132291AB017401571', 3, 50, '基础模块', 'basicset', 80, '员工管理', 'staffdata', 1, 12, '新增关联用户', 'staffdata:adduser', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-19 20:54:28', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-19 20:54:48', 1)");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `salesordernameset` INT(1) NULL DEFAULT '0' COMMENT '销售订单原单号自动编号' AFTER `purchasetype`, ADD COLUMN `salesordernamerule` VARCHAR(50) NULL DEFAULT '' COMMENT '销售订单原单号自动编号规则' AFTER `salesordernameset`,ADD COLUMN `showcontractno` INT(1) NULL DEFAULT '0' COMMENT '合同显示原单号' AFTER `salesordernamerule`,ADD COLUMN `auditsetshow` INT(1) NULL DEFAULT '0' COMMENT '需走审批流程的未审核单的查看权限' AFTER `showcontractno`");

					ps.addBatch("ALTER TABLE `apply_materialdetail` ADD COLUMN `detail_id` VARCHAR(36) NULL DEFAULT '' COMMENT '工单明细ID' AFTER `scheduleid`, ADD INDEX `detail_id` (`detail_id`),ADD COLUMN `oitemid` VARCHAR(36) NULL DEFAULT '' COMMENT '产品ID' AFTER `detail_id`");

					ps.addBatch("ALTER TABLE `salesorder` ADD COLUMN `otherfee` DOUBLE NOT NULL DEFAULT '0' COMMENT '其他费用' AFTER `contractorderid`, ADD COLUMN `otherremark` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '其他备注' AFTER `otherfee`");

					ps.addBatch("ALTER TABLE `salesorder` ADD INDEX `originalbill` (`originalbill`),ADD INDEX `operate_by` (`operate_by`), ADD INDEX `orderid` (`orderid`)");
					ps.addBatch("ALTER TABLE `storein` ADD INDEX `originalbill` (`originalbill`)");
					ps.addBatch("ALTER TABLE `outsourcingin` ADD INDEX `originalbill` (`originalbill`)");
					ps.addBatch("ALTER TABLE `dayinout` ADD INDEX `originalbill` (`originalbill`)");
					ps.addBatch("ALTER TABLE `t_billflow` ADD INDEX `billid` (`billid`)");

					ps.addBatch("ALTER TABLE `datachange_log` ADD INDEX `create_time` (`create_time`)");// erpscan

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `needworkshopset` INT(1) NULL DEFAULT '0' COMMENT '车间必填' AFTER `auditsetshow`, ADD COLUMN `needdeviceset` INT(1) NULL DEFAULT '0' COMMENT '设备必填' AFTER `needworkshopset`,ADD COLUMN `outsourcingset` INT(1) NULL DEFAULT '0' COMMENT '变动产品数量时根据产品的物料BOM自动获取委外材料' AFTER `needdeviceset`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `t_order_detail` ADD COLUMN `d_scheduleid` VARCHAR(36) NULL DEFAULT '' COMMENT '排产ID' AFTER `finishdate`, ADD INDEX `d_scheduleid` (`d_scheduleid`)");
					ps.addBatch("update t_order t ,t_order_detail td set td.d_scheduleid=t.scheduleid where t.id=td.order_id ");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_overtimestaff` (  `detailid` varchar(50) NOT NULL DEFAULT '' COMMENT '编号', `mainid` varchar(36) DEFAULT '' COMMENT '申请ID', `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `staffid` varchar(36) DEFAULT '' COMMENT '申请人',  PRIMARY KEY (`detailid`),  KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`),  KEY `staffid` (`staffid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("ALTER TABLE `apply_overtime` CHANGE COLUMN `operate_by` `operate_by` VARCHAR(3600) NULL DEFAULT '' COMMENT '申请人' AFTER `operate_time`");
					ps.addBatch("ALTER TABLE `apply_config` ADD COLUMN `pricebit` INT NULL DEFAULT '13' COMMENT '单价小数位数' AFTER `overtimetype`, ADD COLUMN `moneybit` INT NULL DEFAULT '2' COMMENT '金额小数位数' AFTER `pricebit`,ADD COLUMN `countbit` INT(11) NULL DEFAULT '4' COMMENT '数量小数位数' AFTER `pricebit`");
					ps.addBatch("INSERT INTO `apply_overtimestaff` (`detailid`, `mainid`, `companyid`, `staffid`) select concat( mainid,'_',substring( operate_by,length( operate_by)-9)), mainid, companyid, operate_by from apply_overtime where length(operate_by)=32");

					ps.addBatch("ALTER TABLE `apply_config` ADD COLUMN `leavereson` VARCHAR(2000) NULL DEFAULT '' COMMENT '请假事由' AFTER `leavetype`, ADD COLUMN `leaveexplain` VARCHAR(500) NULL DEFAULT '' COMMENT '请假说明' AFTER `overtimetype`, ADD COLUMN `overtimeexplain` VARCHAR(500) NULL DEFAULT '' COMMENT '加班说明' AFTER `leaveexplain`, ADD COLUMN `materialexplain` VARCHAR(500) NULL DEFAULT '' COMMENT '补料说明' AFTER `overtimeexplain`, ADD COLUMN `invoiceexplain` VARCHAR(500) NULL DEFAULT '' COMMENT '发票说明' AFTER `materialexplain`, ADD COLUMN `iteminfoexplain` VARCHAR(500) NULL DEFAULT '' COMMENT '商品说明' AFTER `invoiceexplain`, ADD COLUMN `paymentexplain` VARCHAR(500) NULL DEFAULT '' COMMENT '付款说明' AFTER `iteminfoexplain`");

				}

				if (version < 2.8 && newversion >= 2.8) {

					ps.addBatch(" update accountmonth am left join (select f.companyid,f.accountid,f.syear,f.smonth,sum(f.rec_money) as in_money,sum(f.pay_money) as out_money  from (select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,if(ab.bill_type='38',ab.smoney,ab.rec_money) as rec_money,if(ab.bill_type='39',ab.smoney,ab.pay_money) as pay_money from accountbill ab where ab.status='1' and ab.operate_time is not null "
							+ " union all select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,ab.rec_money,ab.pay_money from dayinout ab where ab.status='1'"
							+ " union all select ab.companyid,ab.accountidout as accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,0 as rec_money, ab.smoney as pay_money from transfer ab where ab.status='1'"
							+ " union all select ab.companyid,ab.accountidin as accountid,year(ab.operate_time) as syear ,month(ab.operate_time) as smonth,ab.smoney as rec_money, 0 as pay_money from transfer ab where ab.status='1'"
							+ ") f group by f.companyid,f.accountid,f.syear,f.smonth ) k on am.accountid=k.accountid and am.syear=k.syear and  am.smonth=k.smonth,s_company_config sc set am.in_money=round(ifnull(k.in_money,0),sc.moneybit),am.out_money=round(ifnull(k.out_money,0),sc.moneybit),am.money=round(ifnull(k.in_money,0)-ifnull(k.out_money,0),sc.moneybit)  where  am.companyid=sc.company_id ");

					ps.addBatch("update accountyear am  left join (select f.companyid,f.accountid,f.syear,sum(f.rec_money) as in_money,sum(f.pay_money) as out_money  from (select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,if(ab.bill_type='38',ab.smoney,ab.rec_money) as rec_money,if(ab.bill_type='39',ab.smoney,ab.pay_money) as pay_money from accountbill ab where ab.status='1' and ab.operate_time is not null  union all select ab.companyid,ab.accountid,year(ab.operate_time) as syear ,ab.rec_money,ab.pay_money from dayinout ab where ab.status='1'  union all select ab.companyid,ab.accountidout as accountid,year(ab.operate_time) as syear ,0 as rec_money, ab.smoney as pay_money from transfer ab where ab.status='1' union all select ab.companyid,ab.accountidin as accountid,year(ab.operate_time) as syear ,ab.smoney as rec_money, 0 as pay_money from transfer ab where ab.status='1' ) f  group by f.companyid,f.accountid,f.syear ) k on am.accountid=k.accountid and  am.syear=k.syear,s_company_config sc set am.in_money=round(ifnull"
							+ "(k.in_money,0),sc.moneybit),am.out_money=round(ifnull(k.out_money,0),sc.moneybit),am.money=round(ifnull(k.in_money,0)-ifnull(k.out_money,0),sc.moneybit)  where am.companyid=sc.company_id");

					ps.addBatch("update account s,s_company_config sc set s.money=round(s.beginmoney+ifnull((select sum(am.money) from accountmonth am where am.accountid=s.accountid),0),sc.moneybit) where s.companyid=sc.company_id");

					// 修复物料需求计算下采购申请总数量为0的问题。
					ps.addBatch("update purchase p ,s_company_config s set p.count=round(ifnull((select sum(ps.count) from purchasedetail ps where ps.purchaseid=p.purchaseid),0),s.countbit) where p.companyid=s.company_id");

					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `hasinvoicemodel` TINYINT NULL DEFAULT '0' AFTER `hasapplymodel`");

					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE `prodrequisitiondetail` ADD COLUMN `invoicecount` DOUBLE NULL DEFAULT '0' COMMENT '发票引用数量' AFTER `relationtotalid`,ADD INDEX `invoicecount` (`invoicecount`)");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `invoicestorein` (`invoicestoreinid` varchar(40) NOT NULL COMMENT '编号',   `bill_type` varchar(5) DEFAULT '' COMMENT '单据类型',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `orderid` varchar(36) DEFAULT '' COMMENT '单号',  `invoiceno` varchar(50) DEFAULT '' COMMENT '发票号码',  `operate_time` date DEFAULT NULL COMMENT '入库日期',  `operate_by` varchar(50) DEFAULT '' COMMENT '经手人',  `houseid` varchar(36) DEFAULT '' COMMENT '仓库',  `customerid` varchar(36) DEFAULT '' COMMENT '往来单位',  `count` double DEFAULT '0' COMMENT '数量',  `total` double DEFAULT '0' COMMENT '总额',"
							+ "`totaltax` double DEFAULT '0' COMMENT '总税额',  `totalmoney` double DEFAULT '0' COMMENT '价税总额',  `remark` varchar(200) DEFAULT NULL COMMENT '备注',  `status` varchar(1) DEFAULT NULL COMMENT '状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID',  `update_by` varchar(50) DEFAULT '' COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',   `originalbill` varchar(50) DEFAULT '' COMMENT '原单号',  `oldbill_type` varchar(5) DEFAULT '' COMMENT '原单类型',  `iproperty` varchar(100) DEFAULT '' COMMENT '属性列表',"
							+ "PRIMARY KEY (`invoicestoreinid`),  KEY `bill_type` (`bill_type`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `houseid` (`houseid`),  KEY `orderid` (`orderid`),   KEY `invoiceno` (`invoiceno`),  KEY `customerid` (`customerid`),  KEY `create_id` (`create_id`),  KEY `originalbill` (`originalbill`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `invoicestoreindetail` (  `detailid` varchar(40) NOT NULL COMMENT '编号',  `invoicestoreinid` varchar(40) DEFAULT '' COMMENT '主表编号',   `invoiceno` varchar(50) DEFAULT '' COMMENT '发票号码',  `goods_number` int(11) DEFAULT '0' COMMENT '序号',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `orderid` varchar(36) DEFAULT '' COMMENT '单号',  `operate_time` date DEFAULT NULL COMMENT '入库日期',  `operate_by` varchar(50) DEFAULT '' COMMENT '经手人',  `itemid` varchar(36) DEFAULT '' COMMENT '商品编号',   `customerid` varchar(36) DEFAULT '' COMMENT '往来单位',"
							+ "`houseid` varchar(36) DEFAULT '' COMMENT '仓库',  `price` double DEFAULT '0' COMMENT '单价',  `count` double DEFAULT '0' COMMENT '数量', `total` double DEFAULT '0' COMMENT '金额',  `taxrate` double DEFAULT '0' COMMENT '税率(%)',  `tax` double DEFAULT '0' COMMENT '税额',  `taxprice` double DEFAULT '0' COMMENT '含税单价',  `taxmoney` double DEFAULT '0' COMMENT '价税合计',  `stype` varchar(5) DEFAULT '' COMMENT '类型',  `remark` varchar(200) DEFAULT '' COMMENT '备注',"
							+ "`status` varchar(1) DEFAULT '' COMMENT '状态', `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID', `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) DEFAULT '' COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间', `originalbill` varchar(50) DEFAULT '' COMMENT '原单号',  `batchno` varchar(50) DEFAULT '' COMMENT '批号',  `oldstype` varchar(5) DEFAULT '' COMMENT '原类型',  `relationdetailid` varchar(36) DEFAULT '' COMMENT '关联生产领料明细',  `relationorderid` varchar(36) DEFAULT '' COMMENT '关联生产领料单号',  `relationmainid` varchar(36) DEFAULT '' COMMENT '关联生产领料主表', `relationoperate_time` date DEFAULT NULL COMMENT '关联生产领料日期',"
							+ "PRIMARY KEY (`detailid`),   KEY `invoicestoreinid` (`invoicestoreinid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `itemid` (`itemid`),  KEY `customerid` (`customerid`),  KEY `houseid` (`houseid`),   KEY `create_id` (`create_id`),   KEY `status` (`status`),   KEY `stype` (`stype`),  KEY `operate_by` (`operate_by`),  KEY `orderid` (`orderid`),  KEY `invoiceno` (`invoiceno`),  KEY `relationdetailid` (`relationdetailid`),  KEY `relationorderid` (`relationorderid`),  KEY `relationmainid` (`relationmainid`),   KEY `create_time` (`create_time`),  KEY `update_time` (`update_time`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `invoicestock` ( `stockid` varchar(50) NOT NULL COMMENT '编号',   `companyid` varchar(50) DEFAULT NULL COMMENT '企业编号',  `itemid` varchar(50) DEFAULT NULL COMMENT '商品编号',  `houseid` varchar(50) DEFAULT NULL COMMENT '仓库',  `batchno` varchar(50) DEFAULT '' COMMENT '批号',  `count` double DEFAULT '0' COMMENT '库存数量',  `money` double DEFAULT '0' COMMENT '库存金额', `newcostprice` double DEFAULT '0' COMMENT '成本单价',"
							+ "`stockremark` varchar(200) DEFAULT '' COMMENT '最新单据入库备注',  `enddate` date DEFAULT NULL COMMENT '最后发生日期',  PRIMARY KEY (`stockid`),  UNIQUE KEY `companyid` (`itemid`,`houseid`,`batchno`),  KEY `companyid_1` (`companyid`),  KEY `enddate` (`enddate`),  KEY `count` (`count`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `invoiceprice` (  `invoiceid` varchar(50) NOT NULL COMMENT '编号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号', `itemid` varchar(36) DEFAULT NULL COMMENT '商品编号',  `customerid` varchar(36) DEFAULT NULL COMMENT '往来单位',  `newprice` double DEFAULT '0' COMMENT '最新入库价',  `newdate` date DEFAULT NULL COMMENT '最新入库日期',  `minprice` double DEFAULT '0' COMMENT '最低入库价',  `mindate` date DEFAULT NULL COMMENT '最低价入库日期',  `maxprice` double DEFAULT '0' COMMENT '最高入库价',  `maxdate` date DEFAULT NULL COMMENT '最高价入库日期',  PRIMARY KEY (`invoiceid`),  UNIQUE KEY `itemid_customerid` (`itemid`,`customerid`),  KEY `companyid` (`companyid`),  KEY `newdate` (`newdate`),  KEY `mindate` (`mindate`),  KEY `maxdate` (`maxdate`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `invoiceitemmonth` (  `monthid` varchar(50) NOT NULL COMMENT '编号',  `companyid` varchar(50) DEFAULT NULL COMMENT '企业编号',   `itemid` varchar(50) DEFAULT NULL COMMENT '商品编号',  `houseid` varchar(50) DEFAULT NULL COMMENT '仓库',"
							+ "`batchno` varchar(50) DEFAULT '' COMMENT '批号',  `sdate` date DEFAULT NULL COMMENT '日期',  `syear` int(11) DEFAULT NULL COMMENT '年',  `smonth` int(11) DEFAULT NULL COMMENT '月',  `count` double DEFAULT '0' COMMENT '库存变化数量',   `money` double DEFAULT '0' COMMENT '库存变化金额',  `incount` double DEFAULT '0' COMMENT '累计进数量',  `inmoney` double DEFAULT '0' COMMENT '累计进金额',  `outcount` double DEFAULT '0' COMMENT '累计出数量',  `outmoney` double DEFAULT '0' COMMENT '累计出金额',  PRIMARY KEY (`monthid`),  UNIQUE KEY `com_item_house` (`itemid`,`houseid`,`batchno`,`sdate`),  KEY `companyid_1` (`companyid`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CB3F549280C000011BB9D830279815F0', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 1, '查看', 'invoicestoreindata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:11:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:32:35', 2),"
							+ "('CB3F54C8BAB000019DF47AA01E70DDF0', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 2, '新增', 'invoicestoreindata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:15:14', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:08', 1),"
							+ "('CB3F54CE2A3000016FA41F00DC2317A0', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 3, '详情', 'invoicestoreindata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:15:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:09', 1),"
							+ "('CB3F54D2F1A000014D1720B0FA801D4D', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 4, '复制', 'invoicestoreindata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:15:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:11', 1),"
							+ "('CB3F54D729900001D988166477C0A170', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 10, '主附件查看', 'invoicestoreindata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:16:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:20', 1),"
							+ "('CB3F54DB6AD00001B3DA1120130F13D8', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 5, '作废', 'invoicestoreindata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:16:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:13', 1),"
							+ "('CB3F55014B40000176DADBE0138C1080', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 6, '导出数据', 'invoicestoreindata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:19:05', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:14', 1),"
							+ "('CB3F550BD3A00001836E1E4D391B9800', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 8, '打印', 'invoicestoreindata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:19:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 08:33:15', 1),"
							+ "('CB3F55138A900001CE331560DB61A440', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 9, '主附件上传', 'invoicestoreindata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:20:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:17', 1),"
							+ "('CB3F5522D1C0000130B625E019E04300', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 1, '查看', 'invoicestoreinoutdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:21:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:21', 2),"
							+ "('CB3F553C97400001B01810F01070D990', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 2, '新增', 'invoicestoreinoutdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:23:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:24', 1),"
							+ "('CB3F5540598000012FE817B61CD015D1', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 3, '详情', 'invoicestoreinoutdata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:23:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:26', 1),"
							+ "('CB3F5544180000015255D3501C601EBC', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 10, '主附件查看', 'invoicestoreinoutdata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:23:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:37', 1),"
							+ "('CB3F554831B00001CC441FBC559F17CE', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 4, '复制', 'invoicestoreinoutdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:23:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:28', 1),"
							+ "('CB3F554CE7700001ED4B1A0C4C4B98D0', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 5, '作废', 'invoicestoreinoutdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:24:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:29', 1),"
							+ "('CB3F5551B5E00001D969AFCD110CC490', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 7, '保存', 'invoicestoreinoutdata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:24:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:33', 1),"
							+ "('CB3F555AE16000011480B7D01D2F1664', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 6, '导出数据', 'invoicestoreinoutdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:25:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:32', 1),"
							+ "('CB3F5563CCE00001C88A6728FC376C70', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 9, '主附件上传', 'invoicestoreinoutdata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:25:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:35', 1),"
							+ "('CB3F556F54E00001C8FE46E011D06980', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 1, '查看', 'invoicestoreinoutdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:26:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:33:23', 1),"
							+ "('CB3F55EC066000013E891AE0E0A01E2E', 3, 20, '发票商品管理', 'invoiceinoutmodel', 30, '发票商品库存查询', 'invoicestockdata', 1, 1, '查看', 'invoicestockdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:35:07', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:36:07', 1),"
							+ "('CB3F55FAEDA00001ED5A48D0C9E049A0', 3, 20, '发票商品管理', 'invoiceinoutmodel', 30, '发票商品库存查询', 'invoicestockdata', 1, 2, '导出数据', 'invoicestockdata:exportinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:36:08', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:36:23', 1),"
							+ "('CB3F55FF78500001CDA11D301BBF1196', 3, 20, '发票商品管理', 'invoiceinoutmodel', 60, '发票商品历史价查询', 'invoicepricedata', 1, 1, '查看', 'invoicepricedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:36:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:44:06', 1),"
							+ "('CB3F5607AFD00001F3F8B13CC6401D04', 3, 20, '发票商品管理', 'invoiceinoutmodel', 40, '发票库存进出明细', 'invoiceinoutreportdata', 1, 2, '导出数据', 'invoiceinoutreportdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:37:00', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:37:14', 1),"
							+ "('CB3F560DBCB00001881415A264A91A5C', 3, 20, '发票商品管理', 'invoiceinoutmodel', 40, '发票库存进出明细', 'invoiceinoutreportdata', 1, 1, '查看', 'invoiceinoutreportdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:37:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:40:45', 1),"
							+ "('CB3F56147F100001D6111CCEF8C24710', 3, 20, '发票商品管理', 'invoiceinoutmodel', 50, '发票库存进出汇总', 'invoiceinoutallreportdata', 1, 1, '查看', 'invoiceinoutallreportdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:37:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:44:47', 1),"
							+ "('CB3F56194C10000175791E0013009A20', 3, 20, '发票商品管理', 'invoiceinoutmodel', 60, '发票商品历史价查询', 'invoicepricedata', 1, 2, '导出数据', 'invoicepricedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:38:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:44:09', 1),"
							+ "('CB3F567546E0000133831D301B904480', 3, 20, '发票商品管理', 'invoiceinoutmodel', 50, '发票库存进出汇总', 'invoiceinoutallreportdata', 1, 2, '导出数据', 'invoiceinoutallreportdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:44:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-20 17:44:29', 1),"
							+ "('CB40D2E3590000019E1B1FE01BC01429', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 7, '保存', 'invoicestoreindata:save', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 08:32:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 08:32:59', 1),"
							+ "('CB40D2FC405000016A5011901E20184F', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 8, '打印', 'invoicestoreinoutdata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 08:34:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 08:34:53', 1),"
							+ "('CB40D61E25D00001CE971BAA897014B2', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 11, '详情修改信息', 'invoicestoreindata:modifybill', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 09:29:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 09:30:20', 1),"
							+ "('CB40D63343800001B3582F7BE11B1D7E', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 11, '详情修改信息', 'invoicestoreinoutdata:modifybill', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 09:30:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-25 09:31:15', 1),"
							+ "('CB4194E8CDB00001A5E11640105018C9', 3, 20, '发票商品管理', 'invoiceinoutmodel', 20, '发票商品出库管理', 'invoicestoreinoutdata', 1, 12, '查看单价', 'invoicestoreinoutdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-27 17:03:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-27 17:04:08', 1),"
							+ "('CB4194F52A100001599E47401CB01789', 3, 20, '发票商品管理', 'invoiceinoutmodel', 10, '发票商品入库管理', 'invoicestoreindata', 1, 12, '查看单价', 'invoicestoreindata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-27 17:04:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-06-27 17:04:53', 1),"
							+ "('CB44C90EEDA000018FFC12601C04EB40', 3, 20, '发票商品管理', 'invoiceinoutmodel', 30, '发票商品库存查询', 'invoicestockdata', 1, 3, '查看单价', 'invoicestockdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-07 15:56:53', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-07 15:57:16', 1),"
							+ "('CB44C91F54F0000147A2ABE218E010E3', 3, 20, '发票商品管理', 'invoiceinoutmodel', 40, '发票库存进出明细', 'invoiceinoutreportdata', 1, 3, ' 查看单价', 'invoiceinoutreportdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-07 15:58:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-07 15:58:34', 1),"
							+ "('CB44C9238E8000013CF9155014701948', 3, 20, '发票商品管理', 'invoiceinoutmodel', 50, '发票库存进出汇总', 'invoiceinoutallreportdata', 1, 3, ' 查看单价', 'invoiceinoutallreportdata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-07 15:58:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-07 15:58:44', 1),"
							+ "('CB494BAFA5E000016D8414F0EA401D64', 3, 10, '采购模块', 'storeinmodel', 5, '采购申请管理', 'purchasedata', 1, 17, '详情可修改信息', 'purchasedata:modifyinfo', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-21 16:09:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-21 16:10:26', 1)");

					ps.addBatch("ALTER TABLE `prodrequisitiondetail` ADD COLUMN `invoiceold` TINYINT(1) NULL DEFAULT '0' COMMENT '旧数据' AFTER `invoicecount`, ADD INDEX `invoiceold` (`invoiceold`)");

					ps.addBatch("update purchase set purchasetype='' where purchasetype='null'");
				}

				if (version < 2.9 && newversion >= 2.9) {

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_devicestatus` (  `dsid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',   `deviceid` varchar(36) DEFAULT '' COMMENT '设备ID',  `fstatus` int(1) DEFAULT '0' COMMENT '状态',  `oldfstatus` int(1) DEFAULT '0' COMMENT '原状态',  `remark` varchar(200) DEFAULT NULL COMMENT '变更说明',  `imgurl` varchar(400) DEFAULT '' COMMENT '文件列表',"
							+ "  `filesize` int(11) DEFAULT '0' COMMENT '文件大小',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人', `create_date` datetime DEFAULT NULL COMMENT '创建时间',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建ID',   PRIMARY KEY (`dsid`),  KEY `companyid` (`companyid`),   KEY `deviceid` (`deviceid`),   KEY `create_date` (`create_date`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_device_schedule` ( `id` varchar(36) NOT NULL COMMENT '主键', `fstatus` int(1) DEFAULT '0' COMMENT '状态', `progress_id` varchar(36) DEFAULT '' COMMENT '订单工序ID', `companyid` varchar(50) DEFAULT '' COMMENT '组织编号', `order_id` varchar(50) DEFAULT '' COMMENT '订单ID',"
							+ "`detail_id` varchar(50) DEFAULT '' COMMENT '明细编号', `step_id` varchar(50) DEFAULT '' COMMENT '工序编号',  `step_name` varchar(50) DEFAULT '' COMMENT '工序名称',  `sno` int(11) DEFAULT '1' COMMENT '顺序号',  `s_count` double DEFAULT '0' COMMENT '排产数量',  `finishcount` double DEFAULT '0' COMMENT '完成数量',  `c_count` double DEFAULT '0' COMMENT '转产数量',  `device_id` varchar(36) DEFAULT '' COMMENT '排产设备',"
							+ "`c_device_id` varchar(36) DEFAULT '' COMMENT '转设备',  `remark` varchar(200) DEFAULT '' COMMENT '排产说明', `c_remark` varchar(200) DEFAULT '' COMMENT '转设备说明',  `c_update_id` varchar(36) DEFAULT '' COMMENT '转设备操作人ID',  `c_update_by` varchar(50) DEFAULT '' COMMENT '转设备操作人', `c_update_date` datetime DEFAULT NULL COMMENT '转设备操作日期',  `create_id` varchar(36) DEFAULT '' COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT '' COMMENT '创建人',  `create_date` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) DEFAULT '' COMMENT '更新人ID', `update_by` varchar(50) DEFAULT '' COMMENT '更新人',  `update_date` datetime DEFAULT NULL COMMENT '更新日期',"
							+ " PRIMARY KEY (`id`),  KEY `order_id` (`order_id`),   KEY `companyid` (`companyid`),   KEY `detail_id` (`detail_id`),  KEY `progress_id` (`progress_id`),   KEY `device_id` (`device_id`),   KEY `step_id` (`step_id`),  KEY `sno` (`sno`),  KEY `fstatus` (`fstatus`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备排产表'");

					ps.addBatch("ALTER TABLE `t_device` ADD COLUMN `userid` VARCHAR(36) NULL DEFAULT '' COMMENT '用户ID' AFTER `classid`, ADD INDEX `userid` (`userid`)");

					ps.addBatch("ALTER TABLE `t_progress` ADD COLUMN `s_device_count` DOUBLE NULL DEFAULT '0' COMMENT '设备已排数量' AFTER `work_count`,ADD COLUMN `scheduledate` DATETIME NULL DEFAULT NULL COMMENT '最新排产时间' AFTER `s_device_count`");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "
							+ "('CAC8C9A1763000019B92E0DFBBBB1F32', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 1, '查看', 'scheduledata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:00:17', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:00:52', 1),"
							+ "('CAC8C9ABD4C0000136FFC56B1C68197C', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 2, '排产', 'scheduledata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:00:59', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:01:20', 1),"
							+ "('CAC8C9B1B2B00001D1F71DE588C448F0', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 3, '修改排产记录', 'scheduledata:modify', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:01:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:01:56', 1),"
							+ "('CAC8C9BABDD00001AADD117B9A901000', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 6, '导出数据', 'scheduledata:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:02:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:04:20', 1),"
							+ "('CAC8C9C3774000016217112012A716BD', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 4, '查看设备状态列表', 'scheduledata:showfstatus', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:02:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:04:17', 1),"
							+ "('CAC8C9D5E600000191841F3013307160', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 5, '批量变更设备状态', 'scheduledata:change', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:03:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:04:10', 1),"
							// +
							// "('CAC8CA1793700001F346DDF01BFBE4A0', 3, 3, '看板管理', 'boardset', 90, '设备产能看板', 'boarddevice', 1, 1, '查看', 'boarddevicej:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:08:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:11:05', 1),"
							+ "('CAC8CA17D1100001DBFA39CB23601045', 3, 3, '看板管理', 'boardset', 80, '设备排产看板', 'boardschedule', 1, 1, '查看', 'boardschedule:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:08:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2024-06-17 10:10:22', 1),"
							+ "('CB4A86E4CC600001A35BCF7912104910', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 1, '查看', 'applyovertimetotaldata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 11:58:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 11:59:21', 2),"
							+ "('CB4A86F3C69000011B6311C727C61711', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 2, '新增', 'applyovertimetotaldata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 11:59:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:00:14', 1),"
							+ "('CB4A87101CA00001CF6148FA1000D470', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 3, '修改', 'applyovertimetotaldata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:01:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:01:49', 1),"
							+ "('CB4A87303D1000017151473077501F1D', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 5, '删除', 'applyovertimetotaldata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:03:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:04:34', 1),"
							+ "('CB4A873B15100001B8591A90D23AAF00', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 4, '详情', 'applyovertimetotaldata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:04:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 12:04:18', 1),"
							+ "('CB4A8CD33F900001869067C0194018D2', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 8, '打印', 'applyovertimetotaldata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 13:42:04', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 13:43:33', 1),"
							+ "('CB4A8CD8CB600001218717E7B89018C4', 3, 10, '申请模块', 'applymodel', 55, '加班汇总管理', 'applyovertimetotaldata', 1, 6, '审核', 'applyovertimetotaldata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 13:42:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-07-25 13:42:27', 1),"
							+ "('CB4EC6E78C600001993812F017901FE8', 3, 25, '生产管理', 'orderset', 52, '设备排产管理', 'scheduledata', 1, 5, '批量变更责任人', 'scheduledata:changeuser', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-07 16:58:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-07 16:59:19', 1),"
							+ "('CB4F0DC995B00001A8636D8724FD6260', 3, 1, 'App端', 'appdata', 2, '工单扫描', 'orderscan', 1, 4, '产品标签打印-不能更改包装数量', 'orderscan:productchange', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-08 13:37:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-08 13:38:30', 1),"
							+ "('CB4F0E13D450000177E511951BB898E0', 3, 1, 'App端', 'appdata', 7, '设备任务', 'devicejobs', 1, 1, '查看', 'devicejobs:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-08 13:42:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-08 13:42:40', 1),"
							+ "('CB4F0E2573F000018473F20F1E8019C5', 3, 1, 'App端', 'appdata', 7, '所有任务', 'alljobs', 1, 2, '查看需领料记录', 'alljobs:showbom', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-08 13:43:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-08 13:45:57', 1)");

					ps.addBatch("ALTER TABLE `t_stepnew` ADD COLUMN `issetdevice` TINYINT NULL DEFAULT '0' COMMENT '需设备排产' AFTER `quality`, ADD INDEX `issetdevice` (`issetdevice`)");

					ps.addBatch("ALTER TABLE `t_device_schedule` ADD COLUMN `step_no` INT NULL DEFAULT '0' COMMENT '工序顺序' AFTER `step_id`");

					ps.addBatch("ALTER TABLE `t_device` " + "CHANGE COLUMN `create_date` `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期' AFTER `create_by`,"
							+ "	ADD COLUMN `schedule_id` VARCHAR(36) NULL DEFAULT '' COMMENT '排产人ID' AFTER `create_date`,"
							+ "ADD COLUMN `schedule_by` VARCHAR(36) NULL DEFAULT '' COMMENT '排产人' AFTER `schedule_id`,"
							+ "ADD COLUMN `schedule_date` DATETIME NULL DEFAULT NULL COMMENT '排产日期' AFTER `schedule_by`");

					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `hasdeviceschedule` INT(1) NULL DEFAULT '0' AFTER `hasinvoicemodel`");

					ps.addBatch("ALTER TABLE `s_company` CHANGE COLUMN `hasinvoicemodel` `hasinvoicemodel` INT(1) NULL DEFAULT '0' AFTER `hasapplymodel`");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_overtimetotal` ( `mainid` varchar(36) NOT NULL,  `bill_type` varchar(5) NOT NULL COMMENT '单据类型',  `companyid` varchar(36) NOT NULL COMMENT '企业编号',  `orderid` varchar(50) NOT NULL COMMENT '单据编号',  `operate_time` date NOT NULL COMMENT '申请日期',  `operate_by` varchar(36) NOT NULL COMMENT '经手人',  `beginoverdate` date NOT NULL COMMENT '加班开始日期',  `endoverdate` date NOT NULL COMMENT '加班结束日期',  `remark` varchar(200) DEFAULT NULL COMMENT '备注',  `status` varchar(1) NOT NULL COMMENT '状态',  `printing` int(11) DEFAULT '0' COMMENT '打印次数',   `outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) NOT NULL COMMENT '创建人ID', `create_by` varchar(50) NOT NULL COMMENT '创建人',"
							+ "`create_time` datetime NOT NULL COMMENT '创建时间', `update_id` varchar(36) NOT NULL COMMENT '更新人ID',  `update_by` varchar(50) NOT NULL COMMENT '更新人',  `update_time` datetime NOT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT NULL COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT NULL COMMENT '审核人', `audit_time` datetime DEFAULT NULL COMMENT '审核人时间',  PRIMARY KEY (`mainid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`), KEY `beginoverdate` (`beginoverdate`),  KEY `endoverdate` (`endoverdate`), KEY `status` (`status`), KEY `create_id` (`create_id`),KEY `orderid` (`orderid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='加班申请汇总表'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `apply_overtimetotaldetail` (  `detailid` varchar(36) NOT NULL COMMENT 'id',  `mainid` varchar(36) NOT NULL COMMENT '主表编号', `companyid` varchar(36) NOT NULL COMMENT '企业编号',  `goods_number` int(11) NOT NULL COMMENT '序号',  `status` varchar(1) NOT NULL COMMENT '状态',"
							+ "`remark` varchar(200) DEFAULT NULL COMMENT '备注', `relationmainid` varchar(36) DEFAULT NULL COMMENT '关联加班申请主表id',  `mupdate_id` varchar(36) NOT NULL COMMENT '更新人ID',  `mupdate_by` varchar(50) NOT NULL COMMENT '更新人',   `mupdate_time` datetime NOT NULL COMMENT '更新时间', PRIMARY KEY (`detailid`),  KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`),  KEY `status` (`status`), KEY `relationmainid` (`relationmainid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='加班申请汇总明细表'");
				}

				if (version < 3.0 && newversion >= 3.0) {
					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `canjoint6` INT(1) NULL DEFAULT '0' AFTER `hasdeviceschedule`");
					ps.addBatch("ALTER TABLE `s_company_config`  ADD COLUMN `t6ip` VARCHAR(20) NULL DEFAULT '' AFTER `outsourcingset`, ADD COLUMN `t6datasource` VARCHAR(20) NULL DEFAULT '' AFTER `t6ip`,ADD COLUMN `t6days` INT(3) NULL DEFAULT '0' COMMENT '每月几号及之后不能创建上月前的数据' AFTER `t6datasource`, ADD COLUMN `t6house` INT(1) NULL DEFAULT '0' COMMENT '统一仓库' AFTER `t6days`,ADD COLUMN `t6year` INT(5) NULL DEFAULT '2025' AFTER `t6datasource`,ADD COLUMN `t6user` VARCHAR(50) NULL DEFAULT '' AFTER `t6year`,ADD COLUMN `t6pwd` VARCHAR(50) NULL DEFAULT '' AFTER `t6user`,ADD COLUMN `opcua_username` VARCHAR(50) NULL DEFAULT '' COMMENT '设备连接opcua客户端用户名' AFTER `outsourcingset`,	ADD COLUMN `opcua_password` VARCHAR(50) NULL DEFAULT '' COMMENT '设备连接opcua客户端密码' AFTER `opcua_username`");

					ps.addBatch("ALTER TABLE `storein` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `storemove` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `storeout` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `storecheck` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `splits` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `prodrequisition` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `prodstorage` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `processinout` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `outsourcingin` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `otherinout` ADD COLUMN `dockingOK` INT(1) NULL DEFAULT '0' COMMENT '已对接' AFTER `iproperty`, 	ADD INDEX `dockingOK` (`dockingOK`)");

					ps.addBatch("ALTER TABLE `iteminfo` ADD COLUMN `demand` DOUBLE NULL DEFAULT '1' COMMENT '需求系数' AFTER `hasfile`");

					ps.addBatch("ALTER TABLE `t_device` ADD COLUMN `device_workshop` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '设备车间',ADD COLUMN `device_model` VARCHAR(50) NULL DEFAULT '' COMMENT '设备型号',ADD COLUMN `device_sformat` VARCHAR(50) NULL DEFAULT '' COMMENT '设备规格',ADD COLUMN `device_size` VARCHAR(50) NULL DEFAULT '' COMMENT '外形尺寸',ADD COLUMN `device_weight` VARCHAR(50) NULL DEFAULT '' COMMENT '设备重量',"
							+ "ADD COLUMN `storage_location` VARCHAR(50) NULL DEFAULT '' COMMENT '存放地点',ADD COLUMN `device_price` VARCHAR(50) NULL DEFAULT '' COMMENT '设备原值',ADD COLUMN `device_purpose` VARCHAR(50) NULL DEFAULT '' COMMENT '设备用途',ADD COLUMN `produce_companyname` VARCHAR(50) NULL DEFAULT '' COMMENT '制造厂家全称',ADD COLUMN `produce_date` DATE NULL COMMENT '制造日期',ADD COLUMN `factory_number` VARCHAR(50) NULL DEFAULT '' COMMENT '出厂编号',ADD COLUMN `contract_number` VARCHAR(50) NULL DEFAULT '' COMMENT '合同号',ADD COLUMN `enable_date` DATE NULL COMMENT '投入启用日期',ADD COLUMN `warranty_date` DATE NULL COMMENT '保修日期',"
							+ "ADD COLUMN `purchase_date` DATE NULL COMMENT '采购日期',ADD COLUMN `maintenance_content_day` VARCHAR(300) NULL DEFAULT '' COMMENT '每日保养内容',ADD COLUMN `maintenance_remark_day` VARCHAR(300) NULL DEFAULT '' COMMENT '每日保养备注',ADD COLUMN `maintenance_status_day` INT(11) NULL DEFAULT '0' COMMENT '每日保养状态',ADD COLUMN `maintenance_content_week` VARCHAR(300) NULL DEFAULT '' COMMENT '每周保养内容',ADD COLUMN `maintenance_remark_week` VARCHAR(300) NULL DEFAULT '' COMMENT '每周保养备注',ADD COLUMN `maintenance_status_week` INT(11) NULL DEFAULT '0' COMMENT '每周保养状态',ADD COLUMN `maintenance_content_month` VARCHAR(300) NULL DEFAULT '' COMMENT '每月保养内容',ADD COLUMN `maintenance_remark_month` VARCHAR(300) NULL DEFAULT '' COMMENT '每月保养备注',ADD COLUMN `maintenance_status_month` INT(11) NULL DEFAULT '0' COMMENT '每月保养状态',ADD COLUMN `maintenance_content_season` VARCHAR(300) NULL DEFAULT '' COMMENT '每季保养内容',ADD COLUMN `maintenance_remark_season` VARCHAR(300) NULL DEFAULT '' COMMENT '每季保养备注',ADD COLUMN `maintenance_status_season` INT(11) NULL DEFAULT '0' COMMENT '每季保养状态',ADD COLUMN `maintenance_content_year` VARCHAR(300) NULL DEFAULT '' COMMENT '每年保养内容',ADD COLUMN `maintenance_remark_year` VARCHAR(300) NULL DEFAULT '' COMMENT '每年保养备注',ADD COLUMN `maintenance_status_year` INT(11) NULL DEFAULT '0' COMMENT '每年保养状态'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_devicemaintain` (`maintainid` varchar(36) NOT NULL COMMENT '主键', `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',`fstatus` int(11) DEFAULT NULL COMMENT '维修状态：0-未完成，1-已完成，2-已作废',`related_device_id` varchar(36) DEFAULT NULL COMMENT '关联设备id',`bill_type` varchar(5) DEFAULT NULL COMMENT '单据类型（63）', `orderid` varchar(36) DEFAULT NULL COMMENT '单号（如：DW-2019000000001）',`applyid` varchar(36) DEFAULT NULL COMMENT '关联维修申请id',`apply_orderid` varchar(36) DEFAULT NULL COMMENT '关联维修申请单号', `maintain_cost` double DEFAULT '0' COMMENT '维修金额',"
							+ "`maintain_time` datetime DEFAULT NULL COMMENT '维修完成时间', `maintain_progress` varchar(200) DEFAULT NULL COMMENT '维修进度', `maintain_result` varchar(200) DEFAULT NULL COMMENT '完成后设备运行情况', `remark` varchar(200) DEFAULT NULL COMMENT '备注', `printing` int(11) DEFAULT '0' COMMENT '打印次数', `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(36) DEFAULT NULL COMMENT '创建人', `create_date` datetime DEFAULT NULL COMMENT '创建日期', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',`update_by` varchar(36) DEFAULT NULL COMMENT '更新人',"
							+ "`update_date` datetime DEFAULT NULL COMMENT '更新日期', `maintain_by` varchar(36) DEFAULT NULL COMMENT '维修人（默认开单人或选择员工）', `maintain_date` datetime DEFAULT NULL COMMENT '维修日期', PRIMARY KEY (`maintainid`), KEY `companyid` (`companyid`), KEY `bill_type` (`bill_type`), KEY `fstatus` (`fstatus`), KEY `related_device_id` (`related_device_id`), KEY `applyid` (`applyid`), KEY `maintain_date` (`maintain_date`), KEY `orderid` (`orderid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维修登记表'");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_devicemaintainapply` ( `applyid` varchar(36) NOT NULL COMMENT '主键',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号', `status` int(11) DEFAULT '0' COMMENT '单据状态：0-未审核，1-已审核，2-已作废；默认0',  `fstatus` int(11) DEFAULT '0' COMMENT '登记状态：0-未登记，1-已登记；默认0', `related_device_id` varchar(36) DEFAULT NULL COMMENT '关联设备id', `bill_type` varchar(5) DEFAULT NULL COMMENT '单据类型（62）', `orderid` varchar(36) DEFAULT NULL COMMENT '单号（如：DA-2019000000001）', `customerid` varchar(36) DEFAULT NULL COMMENT '往来单位id（4-本部门）',"
							+ "`remark` varchar(200) DEFAULT NULL COMMENT '备注',`printing` int(11) DEFAULT '0' COMMENT '打印次数', `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `repair_cost` double DEFAULT '0' COMMENT '报修金额', `breakdown_time` datetime DEFAULT NULL COMMENT '故障发生时间', `breakdown_detail` varchar(200) DEFAULT NULL COMMENT '故障描述或损坏原因',`create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(36) DEFAULT NULL COMMENT '创建人', `create_date` datetime DEFAULT NULL COMMENT '创建日期',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID', `update_by` varchar(36) DEFAULT NULL COMMENT '更新人', `update_date` datetime DEFAULT NULL COMMENT '更新日期', `audit_id` varchar(36) DEFAULT NULL COMMENT '审核人ID', `audit_by` varchar(36) DEFAULT NULL COMMENT '审核人',  `audit_date` datetime DEFAULT NULL COMMENT '审核日期', `apply_by` varchar(36) DEFAULT NULL COMMENT '申请人（默认开单人或选择员工）', `apply_date` datetime DEFAULT NULL COMMENT '申请日期', PRIMARY KEY (`applyid`),  KEY `companyid` (`companyid`), KEY `bill_type` (`bill_type`),  KEY `status` (`status`), KEY `fstatus` (`fstatus`), KEY `related_device_id` (`related_device_id`), KEY `orderid` (`orderid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维修申请表'");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_deviceelectricity` (`mainid` varchar(36) NOT NULL COMMENT '主键',  `companyid` varchar(36) DEFAULT '' COMMENT '企业编号',  `electricity_quality_now` double COMMENT '实时电量',  `electricity_consume` double DEFAULT '0' COMMENT '耗电量', `electricity_cost` double COMMENT '预测电费', `electricity_time` date DEFAULT NULL COMMENT '用电日期',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  PRIMARY KEY (`mainid`),  KEY `electricity_time` (`electricity_time`,`mainid`,`create_time`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备电耗表'");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_devicemaintenance` ( `mainid` varchar(36) NOT NULL COMMENT '主键',  `companyid` varchar(50) COMMENT '组织编号',  `bill_type` varchar(5) DEFAULT '61' COMMENT '单据类型', `status` varchar(1) DEFAULT NULL COMMENT '状态', `orderid` varchar(36) COMMENT '单据编号',  `operate_time` date COMMENT '申请日期',  `operate_by` varchar(50) COMMENT '申请人', `maintenance_date` date COMMENT '保养日期', `maintenance_type` int(1) COMMENT '保养类型',  `cstatus` int(1) DEFAULT '0' COMMENT '完成状态', `maintainer_id` varchar(255) DEFAULT NULL COMMENT '保养人ID', `maintainer_by` varchar(255) DEFAULT NULL COMMENT '保养人',  `remark` varchar(255) COMMENT '备注',"
							+ " `printing` int(11) DEFAULT '0' COMMENT '打印次数',  `outexcel` int(11) DEFAULT '0' COMMENT '导出次数',  `create_id` varchar(36) COMMENT '创建人ID',  `create_by` varchar(50) COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) COMMENT '更新人ID', `update_by` varchar(50) COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`mainid`),   KEY `companyid` (`companyid`),  KEY `orderid` (`orderid`),  KEY `maintainer_id` (`maintainer_id`),   KEY `create_id` (`create_id`),  KEY `update_id` (`update_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_devicemaintenancedetail` ( `detailid` varchar(36) NOT NULL COMMENT 'id',  `mainid` varchar(36) COMMENT '主表ID', `companyid` varchar(50) COMMENT '企业编号',  `goods_number` int(11) COMMENT '序号',  `stype` varchar(5) DEFAULT '611' COMMENT '类型',  `device_id` varchar(50) COMMENT '设备编号',  `device_name` varchar(100) COMMENT '设备名称', `device_model` varchar(100) COMMENT '设备型号', `device_location` varchar(100) COMMENT '设备位置', `status` varchar(1) DEFAULT NULL COMMENT '状态', `cstatus` int(1) DEFAULT '0' COMMENT '完成状态',  `plan_maintenance_content` varchar(300) COMMENT '计划维护保养内容', `plan_maintenance_remark` varchar(300) COMMENT '计划维护保养备注',  `actual_maintenance_status` varchar(50) COMMENT '实际完成情况',  `mupdate_id` varchar(36) COMMENT '更新人ID', `mupdate_by` varchar(50) COMMENT '更新人',  `mupdate_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`detailid`),   KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`),   KEY `device_id` (`device_id`),  KEY `device_name` (`device_name`),  KEY `mupdate_id` (`mupdate_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_device_property` ( `detailid` varchar(36) NOT NULL COMMENT 'id', `mainid` varchar(36) DEFAULT NULL COMMENT '主表ID', `companyid` varchar(50) DEFAULT NULL COMMENT '企业编号',`goods_number` int(11) DEFAULT NULL COMMENT '序号', `url` varchar(300) DEFAULT NULL COMMENT 'opcua设备路径',`device_id` varchar(50) DEFAULT NULL COMMENT '设备编号', `property_name` varchar(300) DEFAULT NULL COMMENT '属性名', `property_value` varchar(300) DEFAULT NULL COMMENT '属性值', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(36) DEFAULT NULL COMMENT '创建人', `create_time` datetime DEFAULT NULL COMMENT '创建时间',`latest_update_time` datetime DEFAULT NULL COMMENT '最后更新时间', PRIMARY KEY (`detailid`),   KEY `mainid` (`mainid`),  KEY `companyid` (`companyid`), KEY `device_id` (`device_id`),KEY `create_id` (`create_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES "

							+ "('CB52921A48000001D889179C1D8D6A00', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 0, '查看', 'devicemaintenancedata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 11:45:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:22', 1),"
							+ "('CB529A9BF1F00001F23EF0F0B860CD80', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 1, '新增', 'devicemaintenancedata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 14:14:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:27', 1),"
							+ "('CB529AA621C00001B3AEED801F701015', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 2, '编辑', 'devicemaintenancedata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 14:15:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:33', 1),"
							+ "('CB529AC1C94000018A7F1CC413201C21', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 3, '删除', 'devicemaintenancedata:del', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 14:16:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:39', 1),"
							+ "('CB529ADF5F400001AE949F59138C1253', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 4, '详情', 'devicemaintenancedata:detail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 14:18:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:44', 1),"
							+ "('CB529AE7CE400001B46F490F1BA16580', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 5, '打印', 'devicemaintenancedata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 14:19:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:49', 1),"
							+ "('CB529AEE2EC00001ACF663001AB047D0', 3, 45, '设备模块', 'devicemodel', 10, '设备保养登记', 'devicemaintenancedata', 1, 6, '导出', 'devicemaintenancedata:export', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-19 14:19:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 15:53:55', 1),"
							+ "('CB5533D7C9500001BDA6C100218E13F6', 3, 45, '设备模块', 'devicemodel', 20, '设备保养提醒', 'devicemaintenancemessagedata', 1, 0, '查看', 'devicemaintenancemessagedata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 16:00:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-28 10:27:34', 1),"
							+ "('CB55343756D0000140CD196095F05980', 3, 45, '设备模块', 'devicemodel', 5, '设备台账', 'devicenewdata', 1, 0, '查看', 'devicenewdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 16:06:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-27 16:07:49', 1),"
							+ "('CB55CA59E3A00001EAD2F87F1BC09DD0', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 0, '查看', 'maintainapplydata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:50:29', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:52:13', 1),"
							+ "('CB55CA735A1000012EB4113012601FB7', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 1, '新增', 'maintainapplydata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:52:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:52:45', 1),"
							+ "('CB55CA7B23A00001E48F1A806BE08D80', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 2, '编辑', 'maintainapplydata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:52:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:53:09', 1),"
							+ "('CB55CA811EA0000174219C5EB5E64570', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 3, '删除', 'maintainapplydata:delete', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:53:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:54:17', 1),"
							+ "('CB55CA919F900001C3BD1EB0EB651FAD', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 4, '详情', 'maintainapplydata:detail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:54:17', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:54:35', 1),"
							+ "('CB55CA9602A000017ABA1F391D7E1EA5', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 5, '打印', 'maintainapplydata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:54:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:54:47', 1),"
							+ "('CB55CA9A8A1000011CB31F3013C03740', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 6, '复制新增', 'maintainapplydata:copynew', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:54:54', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:55:18', 1),"
							+ "('CB55CAA071A000016B721CB2FF8C1CA3', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 7, '上传', 'maintainapplydata:mainfileupload', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:55:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:55:55', 1),"
							+ "('CB55CAA97C200001E61056E01F6018E0', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 8, '下载', 'maintainapplydata:mainfileread', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:55:55', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:56:15', 1),"
							+ "('CB55CAAD892000011B2A16DA5CB51384', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 9, '导出汇总', 'maintainapplydata:exporttotal', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:56:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:56:31', 1),"
							+ "('CB55CAB24EA000016B4F192D43B01A03', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 10, '审核', 'maintainapplydata:audit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:56:31', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:56:47', 1),"
							+ "('CB55CAD0E090000160E7DF221FD06510', 3, 45, '设备模块', 'devicemodel', 40, '设备维修申请', 'maintainapplydata', 1, 11, '作废', 'maintainapplydata:status', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:58:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 11:58:53', 1),"
							+ "('CB55D12539E0000187384F40A340182B', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 0, '查看', 'maintaindata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:49:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:49:32', 1),"
							+ "('CB55D12A8AE0000123C91A2E4B6030E0', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 1, '新增', 'maintaindata:new', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:49:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:18', 1),"
							+ "('CB55D12E2C50000145BD13806BA03FF0', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 2, '编辑', 'maintaindata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:49:50', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:20', 1),"
							+ "('CB55D1310550000195461400168C1BC2', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 3, '删除', 'maintaindata:delete', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:24', 1),"
							+ "('CB55D136A46000017A19883015A0F5E0', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 4, '详情', 'maintaindata:detail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:45', 1),"
							+ "('CB55D13BC2500001449E1EAD1C0054D0', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 5, '打印', 'maintaindata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:50:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:02', 1),"
							+ "('CB55D13FBCD00001EEF21F5016B01024', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 6, '复制新增', 'maintaindata:copynew', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:21', 1),"
							+ "('CB55D1446CD000015C551A11F0301A10', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 7, '上传', 'maintaindata:mainfileupload', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:35', 1),"
							+ "('CB55D147D4500001FF8C6F40A8901270', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 8, '下载', 'maintaindata:mainfileread', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:35', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:47', 1),"
							+ "('CB55D14AE5500001E339116F121B1F61', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 9, '导出汇总', 'maintaindata:exporttotal', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:51:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:52:10', 1),"
							+ "('CB55D152E2D0000172F7935017101922', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 10, '审核', 'maintaindata:audit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:52:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:52:37', 1),"
							+ "('CB55D157186000012A2D19BBB760ADD0', 3, 45, '设备模块', 'devicemodel', 50, '设备维修登记', 'maintaindata', 1, 11, '作废', 'maintaindata:status', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:52:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 13:59:01', 1),"
							+ "('CB55D3BBC510000198F0EE0017371080', 3, 45, '设备模块', 'devicemodel', 50, '设备属性', 'devicepropertydata', 1, 0, '查看', 'devicepropertydata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-08-29 14:34:27', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-02 14:34:59', 1),"
							+ "('CB56CFD484C00001CC7ACA3099E01287', 3, 35, '财务模块', 'cashiermodel', 40, '对接财务系统数据', 't6canjoindata', 1, 2, '操作对接数据', 't6canjoindata:save', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-01 16:05:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-09 18:32:04', 1),"
							+ "('CB57CC2CA9B000013CAF15751D001AF9', 3, 3, '看板管理', 'boardset', 100, '设备看板', 'boarddeviceproperty', 1, 1, '查看', 'boarddeviceproperty:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-04 17:30:12', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-09 20:54:03', 1),"
							+ "('CB5908E7143000013BD31D22BC234B70', 3, 45, '设备模块', 'devicemodel', 60, '设备电耗统计', 'deviceelectricitydata', 1, 0, '查看', 'deviceelectricitydata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-08 13:45:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-08 13:46:47', 1),"
							+ "('CB596B467C1000011CAF3A961B2919F9', 3, 35, '财务模块', 'cashiermodel', 40, '对接财务系统数据', 't6canjoindata', 1, 1, '查看', 't6canjoindata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-09 18:30:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-09 18:30:19', 1),"
							+ "('CB59736DD2000001465ADBD836308FF0', 3, 3, '看板管理', 'boardset', 95, 'BI看板', 'boardBI', 1, 1, '查看', 'boardBI:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-09 20:52:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-09 20:53:31', 1),"
							+ "('CB5B4A509CA000015C7B15AD1C906700', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 1, '查看', 'storeoutapplydata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:02:09', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:13:09', 2),"
							+ "('CB5B4A542A000001A3351BE0197A43C0', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 2, '新增', 'storeoutapplydata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:02:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:02:23', 1),"
							+ "('CB5B4A57519000012BD085D0EE581B8B', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 3, '详情', 'storeoutapplydata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:02:36', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:02:36', 1),"
							+ "('CB5B4A5D81B00001EDFD5F191C831550', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 4, '复制', 'storeoutapplydata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:03:02', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:03:02', 1),"
							+ "('CB5B4A64FB400001549973E8100011CD', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 5, '修改', 'storeoutapplydata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:03:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:03:32', 1),"
							+ "('CB5B4A6920A00001AAC2DB8F1A5F189D', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 6, '删除', 'storeoutapplydata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:03:49', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:03:49', 1),"
							+ "('CB5B4A80C8C00001838988C016B05260', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 7, '审核', 'storeoutapplydata:audit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:05:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:05:26', 1),"
							+ "('CB5B4AB814800001D4411A3A1DC078A0', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 8, '反审', 'storeoutapplydata:reaudit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:09:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:09:13', 1),"
							+ "('CB5B4AC182B0000130651E10112039D0', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 9, '锁库存', 'storeoutapplydata:out', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:09:51', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:09:51', 1),"
							+ "('CB5B4AC7A3200001ED3FD1901924C090', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 11, '查看单价', 'storeoutapplydata:showprice', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:10:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:40:57', 1),"
							+ "('CB5B4ACCCB5000015C9F9AD01ED06030', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 12, '导出数据', 'storeoutapplydata:exportdetail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:10:37', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:41:02', 1),"
							+ "('CB5B4AD1F1900001DE3E1D00F1A01B70', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 13, '主附件上传', 'storeoutapplydata:mainfileupload', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:10:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:41:06', 1),"
							+ "('CB5B4AD856A00001F6F25EA01000108A', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 14, '主附件查看', 'storeoutapplydata:mainfileread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:11:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:41:10', 1),"
							+ "('CB5B4AE564C00001BBDAB6401FE2CA90', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 15, '打印', 'storeoutapplydata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-15 14:12:18', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:41:14', 1),"
							+ "('CB5B9C57ABE000011F9A41EB17CFB810', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 1, '查看', 'aftersalesmaintaindata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-16 13:49:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-16 13:51:25', 1),"
							+ "('CB5B9C6E97E00001DB461FC0C000C820', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 2, '新增', 'aftersalesmaintaindata:add', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-16 13:51:32', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 08:58:22', 1),"
							+ "('CB5B9C6ED9600001E8DB1F503CE09230', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 3, '复制', 'aftersalesmaintaindata:copynew', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-16 13:51:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 08:59:03', 1),"
							+ "('CB5B9C6EF4E000014B9711A0170FB270', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 4, '删除', 'aftersalesmaintaindata:del', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-16 13:51:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 08:59:10', 1),"
							+ "('CB5B9C7846200001D36524901D7B1295', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 5, '修改', 'aftersalesmaintaindata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-16 13:52:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 08:59:16', 1),"
							+ "('CB5BDFB69FA00001DDE57C002B171C64', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 6, '配置服务站', 'aftersalesmaintaindata:servicestation', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 09:27:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 15:11:57', 1),"
							+ "('CB5BDFC4E680000169416FD01760126E', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 7, '配置故障原因', 'aftersalesmaintaindata:failurecase', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 09:28:20', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 15:12:35', 1),"
							+ "('CB5BDFC94A10000187A591B91670D930', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 8, '配置故障模式', 'aftersalesmaintaindata:failuremode', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 09:28:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-17 15:12:44', 1),"
							+ "('CB62F4DBF1200001851B1710DBBE77B0', 3, 20, '销售模块', 'storeoutmodel', 8, '发货申请管理', 'storeoutapplydata', 1, 10, '出货', 'storeoutapplydata:canout', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:40:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 09:40:52', 1),"
							+ "('CB5E8605C82000011FE3153D10661A29', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 6, '详情', 'sealdata:detail', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-25 15:01:40', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:16', 1),"
							+ "('CB5FBC7696E0000149FE162DCEDC1E56', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 1, '查看', 'sealdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:27:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-11 10:24:10', 2),"
							+ "('CB5FBC7C46F000017CBEB9B016621210', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 7, '审核', 'sealdata:audit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:27:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:20', 1),"
							+ "('CB5FBC7FB3F00001F280E651C07A3110', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 5, '复制', 'sealdata:copynew', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:27:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:11', 1),"
							+ "('CB5FBC8450E000016A351114E7801463', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 3, '编辑', 'sealdata:edit', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:27:57', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:01', 1),"
							+ "('CB5FBC88E0700001CFE84FEF1FE0A9A0', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 4, '删除', 'sealdata:del', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:28:16', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:08', 1),"
							+ "('CB5FBC8C50700001A72AF3301C50F430', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 2, '新增', 'sealdata:add', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:28:30', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:41:59', 1),"
							+ "('CB5FBC8F7B600001F1A4180013861F04', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 9, '打印', 'sealdata:print', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:28:43', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:27', 1),"
							+ "('CB5FBCAD1D700001269B1540CE29138F', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 8, '作废', 'sealdata:status', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 09:30:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-29 16:42:25', 1),"
							+ "('CB5FBFF2FC300001269814401647A320', 3, 50, '基础模块', 'basicset', 3, '用章管理', 'sealdata', 1, 10, '导出', 'sealdata:outexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 10:27:56', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-29 10:28:11', 1),"
							+ "('CB6028DB19900001159814701ADE5420', 3, 20, '销售模块', 'storeoutmodel', 80, '商品售后管理', 'aftersalesmaintaindata', 1, 9, '导出', 'aftersalesmaintaindata:outexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-30 17:01:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-30 17:02:07', 1),"
							+ "('CB66E6FD8BE00001E0B81353E4EF19B2', 3, 10, '采购模块', 'storeinmodel', 30, '采购历史价格', 'storeinpricedata', 1, 1, '查看', 'storeinpricedata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-21 15:47:42', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-21 16:26:17', 1),"
							+ "('CB66E6F076F000014A5F3DB3776019F1', 3, 10, '采购模块', 'storeinmodel', 30, '采购历史价格', 'storeinpricedata', 1, 2, '导出数据', 'storeinpricedata:excel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-21 15:46:48', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-21 16:26:14', 1),"
							+ "('CB6020F4339000014B161F9288209CA0', 3, 26, '生产报表', 'orderreportset', 36, '工序超期报表', 'orderstepoverdata', 1, 1, '查看', 'orderstepoverdata:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-30 14:43:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-30 14:44:03', 1),"
							+ "('CB6021044C700001F944DF50161A1EF6', 3, 26, '生产报表', 'orderreportset', 36, '工序超期报表', 'orderstepoverdata', 1, 2, '导出数据', 'orderstepoverdata:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-30 14:44:19', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-09-30 14:45:43', 1),"
							+ "('CB6306DAC2C00001E2F7D3FC38E11754', 3, 1, 'App端', 'appdata', 1, '工单查询', 'ordercheck', 1, 2, '作废', 'ordercheck:status', ' ', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 14:48:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-09 14:49:24', 1),"
							+ "('CB63556915300001A3E697C139301E3F', 4, 20, '销售模块', 'storeoutmodel', 6, '销售合并订单管理', 'salesordercombinedata', 1, 1, '查看', 'salesordercombinedata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:41:44', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:42:22', 2),"
							+ "('CB635593C8E000018B43BF26AF40E330', 4, 20, '销售模块', 'storeoutmodel', 6, '销售合并订单管理', 'salesordercombinedata', 1, 2, '新增', 'salesordercombinedata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:44:39', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 14:10:47', 1),"
							+ "('CB63559865200001A3DFF42D1BC058B0', 4, 20, '销售模块', 'storeoutmodel', 6, '销售合并订单管理', 'salesordercombinedata', 1, 3, '修改', 'salesordercombinedata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:44:58', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:45:20', 1),"
							+ "('CB63559E86200001F46AC10030D0F7C0', 4, 20, '销售模块', 'storeoutmodel', 6, '销售合并订单管理', 'salesordercombinedata', 1, 4, '删除', 'salesordercombinedata:del', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:45:23', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:45:44', 1),"
							+ "('CB6355A5B5B000016E9A1E251FD0C610', 4, 20, '销售模块', 'storeoutmodel', 6, '销售合并订单管理', 'salesordercombinedata', 1, 5, '详情', 'salesordercombinedata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:45:52', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:46:08', 1),"
							+ "('CB6355F341D0000169476684CCB0161F', 4, 20, '销售模块', 'storeoutmodel', 6, '销售合并订单管理', 'salesordercombinedata', 1, 6, '导出数据', 'salesordercombinedata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-10 13:51:10', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-15 16:55:31', 1),"
							+ "('CB678DAA4FF000017E50A37C4A70A5C0', 3, 26, '生产报表', 'orderreportset', 100, '工序质检报表', 'stepquality', 1, 2, '导出数据', 'stepquality:toexcel', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-23 16:20:33', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-23 16:21:38', 1),"
							+ "('CB678DB6BBE00001891319971D101A80', 3, 26, '生产报表', 'orderreportset', 100, '工序质检报表', 'stepquality', 1, 1, '查看', 'stepquality:read', '', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-23 16:21:24', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-10-23 16:21:24', 1)");

					ps.addBatch("ALTER TABLE `s_company` ADD COLUMN `hasdevicemodel` INT(1) NULL DEFAULT '0' AFTER `canjoint6`");

					ps.addBatch("ALTER TABLE `prodstoragedetail` ADD COLUMN `isquality` INT(1) NULL DEFAULT '0' COMMENT '是否质检' AFTER `originalbill`,ADD COLUMN `isshowwarn` INT(1) NULL DEFAULT '0' COMMENT '质检提示' AFTER `isquality`, ADD INDEX `stype` (`stype`), ADD INDEX `operate_by` (`operate_by`), ADD INDEX `orderid` (`orderid`), ADD INDEX `isquality` (`isquality`),ADD INDEX `isshowwarn` (`isshowwarn`)");

					ps.addBatch("ALTER TABLE `t_devicemaintenancedetail` ADD COLUMN `relationmainid` VARCHAR(36) NULL DEFAULT NULL COMMENT '关联设备主表id' AFTER `actual_maintenance_status`");

					ps.addBatch("update s_permission set ptype=3 where fvalue='qualitieddata:show3'");

					// ----
					ps.addBatch("CREATE TABLE `after_sales_maintain` ( `aftersalesid` VARCHAR(36) NOT NULL COMMENT '主键', `companyid` VARCHAR(36) NOT NULL COMMENT '企业编号', `status` INT(11) NULL DEFAULT '0' COMMENT '单据状态：0-未处理 1-已处理 2-不处理', `operate_time` DATE NULL DEFAULT NULL COMMENT '单据创建时间（反馈日期）', `orderid` VARCHAR(36) NULL DEFAULT NULL COMMENT '单号，开头：WH', `bill_type` VARCHAR(5) NULL DEFAULT NULL COMMENT '单据类型，如：64', `customerid` VARCHAR(36) NULL DEFAULT NULL COMMENT '关联客户id', `itemid` VARCHAR(36) NULL DEFAULT NULL COMMENT '关联商品id',"
							+ "`servicestationid` VARCHAR(36) NULL DEFAULT NULL COMMENT '关联服务站id', `failuremodeid` VARCHAR(36) NULL DEFAULT NULL COMMENT '关联故障模式id', `failurecaseid` VARCHAR(36) NULL DEFAULT NULL COMMENT '关联故障原因id', `feedback_customer` VARCHAR(50) NULL DEFAULT NULL COMMENT '反馈单位', `feedback_phone` VARCHAR(50) NULL DEFAULT NULL COMMENT '反馈电话', `factory_number` VARCHAR(50) NULL DEFAULT NULL COMMENT '出厂编号', `service_period` VARCHAR(50) NULL DEFAULT NULL COMMENT '使用周期', `failure_symptom` VARCHAR(200) NULL DEFAULT NULL COMMENT '故障现象', `failure_amount` INT(11) NULL DEFAULT NULL COMMENT '故障数量', `address` VARCHAR(200) NULL DEFAULT NULL COMMENT '地址', `operate_by` VARCHAR(50) NULL DEFAULT NULL COMMENT '负责人', `maintain_cost` DOUBLE NULL DEFAULT NULL COMMENT '服务费用', `operate_result` VARCHAR(200) NULL DEFAULT NULL COMMENT '处理结果', `create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', `update_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人ID', `update_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人',"
							+ "`update_date` DATETIME NULL DEFAULT NULL COMMENT '更新日期', PRIMARY KEY (`aftersalesid`), INDEX `idx_companyid` (`companyid`), INDEX `idx_status` (`status`), INDEX `idx_orderid` (`orderid`), INDEX `idx_customerid` (`customerid`), INDEX `idx_itemid` (`itemid`), INDEX `idx_servicestationid` (`servicestationid`), INDEX `idx_failuremodeid` (`failuremodeid`), INDEX `idx_failurecaseid` (`failurecaseid`))COMMENT='售后维护表' COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("CREATE TABLE `service_station` ( `servicestationid` VARCHAR(36) NOT NULL COMMENT '主键', `companyid` VARCHAR(36) NOT NULL COMMENT '企业编号', `service_station_number` VARCHAR(50) NULL DEFAULT NULL COMMENT '服务站编号', `service_station_name` VARCHAR(50) NULL DEFAULT NULL COMMENT '服务站名称', `create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', `update_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人ID', `update_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人', `update_date` DATETIME NULL DEFAULT NULL COMMENT '更新日期', PRIMARY KEY (`servicestationid`), INDEX `idx_companyid` (`companyid`))COMMENT='服务站表' COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("CREATE TABLE `failure_case` ( `failurecaseid` VARCHAR(36) NOT NULL COMMENT '主键', `companyid` VARCHAR(36) NOT NULL COMMENT '企业编号', `failure_case_code` VARCHAR(200) NULL DEFAULT NULL COMMENT '故障原因代码', `failure_case` VARCHAR(200) NULL DEFAULT NULL COMMENT '故障原因名称', `create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', `update_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人ID', `update_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人', `update_date` DATETIME NULL DEFAULT NULL COMMENT '更新日期', PRIMARY KEY (`failurecaseid`), INDEX `idx_companyid` (`companyid`))COMMENT='故障原因表' COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("CREATE TABLE `failure_mode` ( `failuremodeid` VARCHAR(36) NOT NULL COMMENT '主键', `companyid` VARCHAR(36) NOT NULL COMMENT '企业编号', `failure_mode_code` VARCHAR(200) NULL DEFAULT NULL COMMENT '故障模式编号', `failure_mode` VARCHAR(200) NULL DEFAULT NULL COMMENT '故障模式名称', `create_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人ID', `create_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '创建人', `create_date` DATETIME NULL DEFAULT NULL COMMENT '创建日期', `update_id` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人ID', `update_by` VARCHAR(36) NULL DEFAULT NULL COMMENT '更新人', `update_date` DATETIME NULL DEFAULT NULL COMMENT '更新日期', PRIMARY KEY (`failuremodeid`), INDEX `idx_companyid` (`companyid`))COMMENT='故障模式表' COLLATE='utf8_general_ci' ENGINE=InnoDB");

					ps.addBatch("ALTER TABLE `iteminfo` ADD COLUMN `ist6has` INT(1) NULL DEFAULT '0' COMMENT '检测T6是否存在' AFTER `demand`, ADD INDEX `ist6has` (`ist6has`)");

					ps.addBatch("ALTER TABLE `after_sales_maintain` CHANGE COLUMN `customerid` `customerid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联客户id' AFTER `bill_type`, CHANGE COLUMN `itemid` `itemid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联商品id' AFTER `customerid`, CHANGE COLUMN `servicestationid` `servicestationid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联服务站id' AFTER `itemid`, CHANGE COLUMN `failuremodeid` `failuremodeid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联故障模式id' AFTER `servicestationid`, CHANGE COLUMN `failurecaseid` `failurecaseid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联故障原因id' AFTER `failuremodeid`,"
							+ "CHANGE COLUMN `feedback_customer` `feedback_customer` VARCHAR(50) NULL DEFAULT '' COMMENT '反馈单位' AFTER `failurecaseid`, CHANGE COLUMN `feedback_phone` `feedback_phone` VARCHAR(50) NULL DEFAULT '' COMMENT '反馈电话' AFTER `feedback_customer`, CHANGE COLUMN `factory_number` `factory_number` VARCHAR(50) NULL DEFAULT '' COMMENT '出厂编号' AFTER `feedback_phone`, CHANGE COLUMN `service_period` `service_period` VARCHAR(50) NULL DEFAULT '' COMMENT '使用周期' AFTER `factory_number`, 	CHANGE COLUMN `failure_symptom` `failure_symptom` VARCHAR(200) NULL DEFAULT '' COMMENT '故障现象' AFTER `service_period`, CHANGE COLUMN `address` `address` VARCHAR(200) NULL DEFAULT '' COMMENT '地址' AFTER `failure_amount`, CHANGE COLUMN `operate_by` `operate_by` VARCHAR(50) NULL DEFAULT '' COMMENT '负责人' AFTER `address`, 	CHANGE COLUMN `maintain_cost` `maintain_cost` DOUBLE NULL DEFAULT '0' COMMENT '服务费用' AFTER `operate_by`, CHANGE COLUMN `operate_result` `operate_result` VARCHAR(200) NULL DEFAULT '' COMMENT '处理结果' AFTER `maintain_cost`");

					ps.addBatch("ALTER TABLE `after_sales_maintain`  ADD COLUMN `iteminfo` VARCHAR(200) NULL DEFAULT '' COMMENT '商品型号' AFTER `operate_result`, ADD COLUMN `remark` VARCHAR(300) NULL DEFAULT '' COMMENT '备注' AFTER `iteminfo`, 	ADD COLUMN `username` VARCHAR(100) NULL DEFAULT '' COMMENT '用户姓名' AFTER `remark`, 	ADD COLUMN `userphone` VARCHAR(100) NULL DEFAULT '' COMMENT '联系电话' AFTER `username`, ADD COLUMN `useraddr` VARCHAR(500)  NULL DEFAULT '' COMMENT '用户地址' AFTER `userphone`");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `t_device_finishcount_record` ( `id` varchar(36) NOT NULL COMMENT 'id',  `companyid` varchar(50) DEFAULT NULL COMMENT '企业编号',  `relationdevicemainid` varchar(36) DEFAULT NULL COMMENT '设备主表id',  `device_id` varchar(50) DEFAULT NULL COMMENT '设备编号',  `count_record` int(1) DEFAULT NULL COMMENT '数量记录',  `sourcetimestamp` timestamp(3) NULL DEFAULT NULL COMMENT 'sourcetimestamp',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',   PRIMARY KEY (`id`),   KEY `relationdevicemainid` (`relationdevicemainid`),  KEY `companyid` (`companyid`),  KEY `id` (`id`,`create_time`),  KEY `sourcetimestamp` (`sourcetimestamp`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `storeoutapply` (  `storeoutapplyid` varchar(50) NOT NULL COMMENT '编号',  `bill_type` varchar(5) DEFAULT NULL COMMENT '单据类型',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `orderid` varchar(36) DEFAULT NULL COMMENT '单号', `operate_time` date DEFAULT NULL COMMENT '出库日期',  `operate_by` varchar(50) DEFAULT NULL COMMENT '经手人',  `houseid` varchar(36) DEFAULT NULL COMMENT '仓库', `customerid` varchar(36) DEFAULT NULL COMMENT '往来单位',  `currency` varchar(50) DEFAULT '人民币' COMMENT '币种', `count` double DEFAULT '0' COMMENT '数量',  `total` double DEFAULT '0' COMMENT '总额',  `totaltax` double DEFAULT '0' COMMENT '总税额',  `totalmoney` double DEFAULT '0' COMMENT '价税总额', `remark` varchar(200) DEFAULT NULL COMMENT '备注',  `status` varchar(1) DEFAULT NULL COMMENT '状态', `printing` int(11) DEFAULT '0' COMMENT '打印次数', `outexcel` int(11) DEFAULT '0' COMMENT '导出次数', `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间', `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',"
							+ "`update_by` varchar(50) DEFAULT NULL COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID',  `audit_by` varchar(50) DEFAULT '' COMMENT '审核人',  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',  `originalbill` varchar(50) DEFAULT NULL COMMENT '原单号', `iproperty` varchar(100) DEFAULT NULL COMMENT '属性列表',  `dockingOK` int(1) DEFAULT '0' COMMENT '已对接',  `linkman` varchar(50) DEFAULT '' COMMENT '联系人',  `linkphone` varchar(50) DEFAULT '' COMMENT '联系电话',  `deliveryadrr` varchar(200) DEFAULT '' COMMENT '收货地址', `deliverer` varchar(50) DEFAULT '' COMMENT '送货人',"
							+ "`license` varchar(50) DEFAULT '' COMMENT '车号',  `receiver` varchar(50) DEFAULT '' COMMENT '收货人',  PRIMARY KEY (`storeoutapplyid`),  KEY `bill_type` (`bill_type`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `houseid` (`houseid`),  KEY `customerid` (`customerid`),  KEY `status` (`status`),  KEY `create_id` (`create_id`),  KEY `currency` (`currency`),   KEY `dockingOK` (`dockingOK`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("CREATE TABLE IF NOT EXISTS `storeoutapplydetail` (  `detailid` varchar(36) NOT NULL COMMENT '编号',  `storeoutapplyid` varchar(36) DEFAULT NULL COMMENT '主表编号',  `goods_number` int(11) DEFAULT NULL COMMENT '序号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号', `orderid` varchar(36) DEFAULT NULL COMMENT '单号',  `operate_time` date DEFAULT NULL COMMENT '出库日期',  `operate_by` varchar(50) DEFAULT NULL COMMENT '经手人',  `itemid` varchar(36) DEFAULT NULL COMMENT '商品编号',"
							+ "`customerid` varchar(36) DEFAULT NULL COMMENT '往来单位',  `houseid` varchar(36) DEFAULT NULL COMMENT '仓库', `price` double DEFAULT NULL COMMENT '单价', `count` double DEFAULT NULL COMMENT '数量', `total` double DEFAULT NULL COMMENT '金额',  `taxrate` double DEFAULT '0' COMMENT '税率(%)', `tax` double DEFAULT '0' COMMENT '税额',  `taxprice` double DEFAULT '0' COMMENT '含税单价',"
							+ "`taxmoney` double DEFAULT '0' COMMENT '价税合计', `stype` varchar(5) DEFAULT NULL COMMENT '类型', `remark` varchar(200) DEFAULT NULL COMMENT '备注',  `status` varchar(1) DEFAULT NULL COMMENT '状态',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID', `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人', `update_time` datetime DEFAULT NULL COMMENT '更新时间',  `audit_id` varchar(36) DEFAULT '' COMMENT '审核人ID', `audit_by` varchar(50) DEFAULT '' COMMENT '审核人', `audit_time` datetime DEFAULT NULL COMMENT '审核时间', `originalbill` varchar(50) DEFAULT NULL COMMENT '原单号', `batchno` varchar(50) DEFAULT '' COMMENT '批号', `outcount` double DEFAULT '0' COMMENT '已出库数', `returncount` double DEFAULT '0' COMMENT '已退数量',  `notoutcount` double DEFAULT '0' COMMENT '未出库数', `oldcount` double DEFAULT '0' COMMENT '计划数量', `returndetailid` varchar(36) DEFAULT '' COMMENT '退货源申请单',  `returnorderid` varchar(36) DEFAULT '' COMMENT '退货源申请单号',  `relationdetailid` varchar(36) DEFAULT '' COMMENT '关联销售订单',"
							+ "`relationorderid` varchar(36) DEFAULT '' COMMENT '关联销售订单单号',  `relationmainid` varchar(36) DEFAULT '' COMMENT '关联销售订单主表',  PRIMARY KEY (`detailid`),  KEY `storeoutapplyid` (`storeoutapplyid`),  KEY `companyid` (`companyid`),  KEY `operate_time` (`operate_time`),  KEY `itemid` (`itemid`),  KEY `customerid` (`customerid`),  KEY `houseid` (`houseid`),  KEY `status` (`status`),  KEY `stype` (`stype`),  KEY `notoutcount` (`notoutcount`),  KEY `create_id` (`create_id`),  KEY `returndetailid` (`returndetailid`),  KEY `relationdetailid` (`relationdetailid`),  KEY `relationmainid` (`relationmainid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
					ps.addBatch("ALTER TABLE `storeoutdetail` ADD COLUMN `relationtype` INT(1) NOT NULL DEFAULT '0' COMMENT '关联类型' AFTER `relationmainid`,ADD COLUMN `applyrelationdetailid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联发货申请' AFTER `relationtype`, ADD COLUMN `applyrelationorderid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联发货申请单号' AFTER `applyrelationdetailid`, 	ADD COLUMN `applyrelationmainid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联发货申请主表' AFTER `applyrelationorderid`, ADD INDEX `applyrelationmainid` (`applyrelationmainid`), ADD INDEX `applyrelationdetailid` (`applyrelationdetailid`)");
					// ----

					// -- 2025-10-22

					ps.addBatch("CREATE TABLE IF NOT EXISTS `salesordercombine` ( `salesordercombineid` varchar(36) NOT NULL COMMENT '编号',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `orderid` varchar(36) DEFAULT NULL COMMENT '单据编号',  `operate_time` date DEFAULT NULL COMMENT '单据日期',  `customerid` varchar(36) DEFAULT NULL COMMENT '客户id',  `customername` varchar(36) DEFAULT NULL COMMENT '客户名称',   `count` double DEFAULT '0' COMMENT '总数量',  `total` double DEFAULT '0' COMMENT '总金额',  `tax` double DEFAULT '0' COMMENT '总税额',"
							+ "`taxmoney` double DEFAULT '0' COMMENT '价税总额',  `remark` varchar(200) DEFAULT '' COMMENT '备注',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  PRIMARY KEY (`salesordercombineid`),  KEY `companyid` (`companyid`),  KEY `customerid` (`customerid`),   KEY `create_id` (`create_id`),  KEY `update_id` (`update_id`),  KEY `orderid` (`orderid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8");

					ps.addBatch("ALTER TABLE `s_company_config` ADD COLUMN `seallist` VARCHAR(1000) NULL DEFAULT '' COMMENT '用章配置'");
					ps.addBatch("CREATE OR REPLACE  VIEW s_companyconfig as select  c.*,cc.*,if(cc.lifelimit='2',null,cc.enddate) as newenddate from s_company c left join s_company_config cc  on c.id = cc.company_id where c.s_company_type='1'");

					ps.addBatch("ALTER TABLE service_station ADD COLUMN `province` VARCHAR(50) NULL DEFAULT '' COMMENT '省份名称', ADD COLUMN `operate_by` VARCHAR(50) NULL DEFAULT '' COMMENT '负责人', ADD COLUMN `phone` VARCHAR(50) NULL DEFAULT '' COMMENT '手机号码', ADD COLUMN `telephone` VARCHAR(50) NULL DEFAULT '' COMMENT '电话号码', ADD COLUMN `address` VARCHAR(100) NULL DEFAULT '' COMMENT '地址', ADD COLUMN `deposit_bank` VARCHAR(100) NULL DEFAULT '' COMMENT '开户银行', ADD COLUMN `bank_account` VARCHAR(100) NULL DEFAULT '' COMMENT '银行账号', ADD COLUMN `service_station_code` VARCHAR(50) NULL DEFAULT '' COMMENT '服务站代码', ADD COLUMN `remark` VARCHAR(200) NULL DEFAULT '' COMMENT '备注'");
					ps.addBatch("ALTER TABLE after_sales_maintain ADD COLUMN `fstatus` INT(11) NULL DEFAULT '0' COMMENT '支付状态,0-未支付,1-已支付,2-无需支付'");
					ps.addBatch("CREATE TABLE IF NOT EXISTS `seal_apply` ( `sealid` varchar(36) NOT NULL COMMENT '主键',  `companyid` varchar(36) DEFAULT NULL COMMENT '企业编号',  `bill_type` varchar(5) DEFAULT NULL COMMENT '40',  `status` int(11) DEFAULT NULL COMMENT '审核状态：0-未审核，1-已审核，2-已作废，3-审批中',  `orderid` varchar(36) DEFAULT NULL COMMENT '单号-SA',  `seal` varchar(100) DEFAULT NULL COMMENT '用章类型',  `apply_by` varchar(36) DEFAULT NULL COMMENT '申请人',  `apply_id` varchar(36) DEFAULT NULL COMMENT '申请人id',  `apply_date` date DEFAULT NULL COMMENT '申请日期',  `customerid` varchar(36) DEFAULT NULL COMMENT '关联本单位部门id',  `operate_date` date DEFAULT NULL COMMENT '用章日期',"
							+ "`apply_remark` varchar(300) DEFAULT NULL COMMENT '申请说明',  `create_id` varchar(36) DEFAULT NULL COMMENT '创建人ID',  `create_by` varchar(36) DEFAULT NULL COMMENT '创建人',  `create_date` datetime DEFAULT NULL COMMENT '创建日期',  `update_id` varchar(36) DEFAULT NULL COMMENT '更新人ID',   `update_by` varchar(36) DEFAULT NULL COMMENT '更新人',  `update_date` datetime DEFAULT NULL COMMENT '更新日期',  `audit_id` varchar(36) DEFAULT NULL COMMENT '审核人ID',  `audit_by` varchar(36) DEFAULT NULL COMMENT '审核人', `audit_date` datetime DEFAULT NULL COMMENT '审核日期',  PRIMARY KEY (`sealid`), KEY `idx_companyid` (`companyid`),  KEY `idx_status` (`status`), KEY `idx_seal` (`seal`),  KEY `idx_customerid` (`customerid`), KEY `idx_apply_id` (`apply_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用章申请表'");
					ps.addBatch("CREATE TABLE `storeinprice` ( `storeinpriceid` VARCHAR(50) NOT NULL COMMENT '编号', `companyid` VARCHAR(36) NULL DEFAULT NULL COMMENT '企业编号', 	`itemid` VARCHAR(36) NULL DEFAULT NULL COMMENT '商品编号', `customerid` VARCHAR(36) NULL DEFAULT NULL COMMENT '往来单位', `newprice` DOUBLE NULL DEFAULT '0' COMMENT '最新入库价', `newdate` DATE NULL DEFAULT NULL COMMENT '最新入库日期', `minprice` DOUBLE NULL DEFAULT '0' COMMENT '最低入库价', `mindate` DATE NULL DEFAULT NULL COMMENT '最低价入库日期', `maxprice` DOUBLE NULL DEFAULT '0' COMMENT '最高入库价', `maxdate` DATE NULL DEFAULT NULL COMMENT '最高价入库日期', PRIMARY KEY (`storeinpriceid`), UNIQUE INDEX `itemid_customerid` (`itemid`, `customerid`), INDEX `companyid` (`companyid`), INDEX `newdate` (`newdate`), INDEX `mindate` (`mindate`), INDEX `maxdate` (`maxdate`))COLLATE='utf8_general_ci' ENGINE=InnoDB");
					ps.addBatch("INSERT INTO storeinprice ( storeinpriceid,     companyid,    itemid,   customerid,    newprice,  newdate ) SELECT   UUID() AS storeinpriceid,   sd1.companyid,   sd1.itemid,   sd1.customerid,  sd1.taxprice AS newprice,  sd1.operate_time AS newdate FROM storeindetail sd1 WHERE sd1.status = '1' and sd1.stype = '11'   AND NOT EXISTS (  SELECT 1    FROM storeindetail sd2 "
							+ " WHERE sd2.itemid = sd1.itemid   AND sd2.customerid = sd1.customerid  AND sd2.status = '1' and sd2.stype = '11'   AND (  sd2.operate_time > sd1.operate_time    OR (sd2.operate_time = sd1.operate_time AND sd2.update_time > sd1.update_time)  ) ) ON DUPLICATE KEY UPDATE    newprice = IF(STRCMP(VALUES(newdate), storeinprice.newdate) >= 0 OR storeinprice.newdate IS NULL,      VALUES(newprice),    storeinprice.newprice),   newdate = IF(STRCMP(VALUES(newdate), storeinprice.newdate) >= 0 OR storeinprice.newdate IS NULL,    VALUES(newdate),    storeinprice.newdate)");

					ps.addBatch("UPDATE storeinprice sp JOIN (   SELECT    sd1.itemid,    sd1.customerid,   sd1.taxprice AS maxprice,   sd1.operate_time AS maxdate  FROM storeindetail sd1  WHERE sd1.status = '1' and sd1.stype = '11'  AND NOT EXISTS (   SELECT 1 FROM storeindetail sd2  WHERE sd2.itemid = sd1.itemid   AND sd2.customerid = sd1.customerid   AND sd2.status = '1' and sd2.stype = '11'   AND (sd2.taxprice > sd1.taxprice    OR (sd2.taxprice = sd1.taxprice AND sd2.update_time < sd1.update_time))  ) ) highest ON sp.itemid = highest.itemid AND sp.customerid = highest.customerid SET   sp.maxprice = highest.maxprice,   sp.maxdate = highest.maxdate");
					ps.addBatch("UPDATE storeinprice sp JOIN (   SELECT    sd1.itemid,   sd1.customerid,    sd1.taxprice AS minprice,   sd1.operate_time AS mindate  FROM storeindetail sd1   WHERE sd1.status = '1' and sd1.stype = '11'   AND NOT EXISTS (   SELECT 1 FROM storeindetail sd2    WHERE sd2.itemid = sd1.itemid    AND sd2.customerid = sd1.customerid   AND sd2.status = '1' and sd2.stype = '11'   AND (sd2.taxprice < sd1.taxprice     OR (sd2.taxprice = sd1.taxprice AND sd2.update_time < sd1.update_time))   ) ) lowest ON sp.itemid = lowest.itemid AND sp.customerid = lowest.customerid SET   sp.minprice = lowest.minprice,    sp.mindate = lowest.mindate");
					// -- 子查询3：最低价
					ps.addBatch("update storeinprice set storeinpriceid = REPLACE(storeinpriceid, '-', '') where 1=1");
					ps.addBatch("ALTER TABLE `salesorderdetail` ADD COLUMN `relationcombineid` VARCHAR(36) NULL DEFAULT '' COMMENT '关联合并销售订单主表id' , ADD COLUMN `combine_goods_number` INT(11) NULL DEFAULT '0' COMMENT '关联合并销售订单序号' AFTER `relationcombineid`");

					ps.addBatch("ALTER TABLE `t_device_finishcount_record` ADD COLUMN `svalue` INT(11) NULL DEFAULT '1' AFTER `create_time`");

					ps.addBatch("ALTER TABLE `customer` ADD INDEX `create_time` (`create_time`) , ADD INDEX `customercode` (`customercode`)");

					ps.addBatch("update  s_permission s  set s.parentname='发票商品' where s.parentvalue='invoiceinoutmodel'");

					ps.addBatch("update t_device_schedule ts,s_company_config sc set  ts.finishcount=ifnull((select sum(tp.detail_count) from t_order_progress tp where "
							+ "tp.progress_id=ts.progress_id and tp.device_id=ts.device_id and tp.fstatus=1 ),sc.countbit) where ts.companyid=sc.company_id and ts.s_count>ts.c_count");
					ps.addBatch("update t_device_schedule ts,s_company_config sc  set  ts.finishcount= if(ts.finishcount>round(ts.s_count-ts.c_count,sc.countbit),round(ts.s_count-ts.c_count,sc.countbit),ts.finishcount),ts.fstatus=if(ts.finishcount>=round(ts.s_count-ts.c_count,sc.countbit),1,0) where ts.companyid=sc.company_id and ts.s_count>ts.c_count");

					ps.addBatch("ALTER TABLE `t_step` ADD COLUMN `pro_remark` VARCHAR(200) NULL DEFAULT '' COMMENT '班产量' AFTER `out_price`");

					ps.addBatch("delete from s_permission  where id='C8B7B89DCFA00001E64A1960177B1634'");
					ps.addBatch("delete from s_roles_permission where functionid='C8B7B89DCFA00001E64A1960177B1634'");

					// 商品售后转私有
					ps.addBatch("update s_permission set ptype=4 where functionvalue ='aftersalesmaintaindata'");

					ps.addBatch("update s_permission set fname='导出数据' where id='CADFB2B0DB20000142741D40E0B01296'");
					ps.addBatch("update s_permission set fname='查看单价' where id='C8C71D9550200001276C1BA012A27CF0'");
				}
				
				if (version < 3.1 && newversion >= 3.1) {
					// 角色预设权限
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB741C59AAE000017052F785FB005A40', 4, 100, '系统管理', 'systemset', 50, '角色权限管理', 'roledata', 1, 9, '导出数据', 'userdata:export', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-01 16:41:15', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-01 16:41:29', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB71D3A575A00001F6BE1F0070345B30', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 8, '使用预设', 'rolepresetdata:presetapply', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-24 14:22:48', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-25 09:23:44', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D482C2B800001732C1130F5A91BFD', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 7, '配置保存', 'rolepresetdata:rulesave', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 11:29:38', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 11:29:48', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D4825BEF00001F47ABC505C7723B0', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 6, '配置查看', 'rolepresetdata:ruleread', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 11:29:11', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 11:29:33', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D481FB9F00001C59724006A621D7B', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 5, '启停用', 'rolepresetdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 11:28:47', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 11:29:10', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D45BF041000018DF67700F550E3B0', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 4, '修改', 'rolepresetdata:edit', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:47:13', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:49:25', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D45D9CB900001F45D1F301B007EE0', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 3, '删除', 'rolepresetdata:delete', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:49:03', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:49:03', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D45D55E300001BFA830B3AE40D2E0', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 2, '新增', 'rolepresetdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:48:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:48:45', 1)");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB6D45CFB29000018A36171014C31B5C', 1, 100, '系统管理', 'systemset', 8, '角色预设管理', 'rolepresetdata', 1, 1, '查看', 'rolepresetdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:48:22', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-11-10 10:48:22', 1)");

					// 添加字段备注2，时长
					ps.addBatch(" ALTER TABLE `t_order_progress` " +
							" ADD COLUMN `remark_2` VARCHAR(200) NULL DEFAULT NULL COMMENT '备注' AFTER `invalid_nos`, " +
							" ADD COLUMN `duration` DOUBLE NULL DEFAULT NULL COMMENT '时长' AFTER `remark_2` ");

					ps.addBatch("ALTER TABLE `t_order_progress` " +
							" CHANGE COLUMN `duration` `duration` DOUBLE NULL DEFAULT '0' COMMENT '时长' AFTER `remark_2` ");

					ps.addBatch("ALTER TABLE `prodrequisition` ADD COLUMN `schedule_pick_id` VARCHAR(36) NULL DEFAULT '' COMMENT '排产领料主表id' AFTER `worksheetbatchno`, ADD COLUMN `schedule_pick_billno` VARCHAR(50) NULL DEFAULT '' COMMENT '排产领料单号' AFTER `schedule_pick_id`");
					ps.addBatch("ALTER TABLE `prodrequisition` ADD INDEX `schedule_pick` (`schedule_pick_id`)");

					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB436A900001A32218F6877D16C0', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 8, '打印', 'schedulepickdata:print', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:05:10', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:05:28', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB3C5B900001A015107010F01033', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理管理', 'schedulepickdata', 1, 7, '导出明细', 'schedulepickdata:exportdetail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:04:41', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:05:10', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB3885A000011684F40014FC6B50', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 6, '导出汇总', 'schedulepickdata:exporttotal', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:04:25', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:04:56', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB33C0A0000149FB93E0134CFE10', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 5, '作废', 'schedulepickdata:status', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:04:06', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:04:25', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB2EA8A00001A8DE187E78306690', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 4, '复制', 'schedulepickdata:copynew', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:03:45', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:04:05', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB1A47100001B99C7E001B601553', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 3, '详情', 'schedulepickdata:detail', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:02:21', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:03:33', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB1571100001A7351F3019301929', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 2, '新增', 'schedulepickdata:new', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:02:01', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:03:17', 1);");
					ps.addBatch("INSERT INTO `s_permission` (`id`, `ptype`, `pseq`, `parentname`, `parentvalue`, `fseq`, `functionname`, `functionvalue`, `fstatus`, `seq`, `fname`, `fvalue`, `description`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`, `datarule`) VALUES ('CB76EB0CD71000015ECF481086501BC2', 3, 30, '库存模块', 'storemodel', 1, '排产领料管理', 'schedulepickdata', 1, 1, '查看', 'schedulepickdata:read', NULL, 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:01:26', 'C88CD36C3120000167D61420119C5FB0', '后台管理员[superadmin]', '2025-12-10 10:03:10', 2);");

					ps.addBatch("ALTER TABLE `prodrequisitiondetail` ADD COLUMN `schedule_pick_billno` VARCHAR(50) NULL DEFAULT '' COMMENT '排产领料主表order' AFTER `invoiceold`");
				}

				ps.addBatch("update sysconfigure set version=" + newversion + " where confid='" + confid + "'");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);

				if (version < 1.1 && newversion >= 1.1) {
					// 初始化生产需领料
					Fileoperate.initT_orderNeed(params, context);
					// 初始化工序功能
					Newstep.updateoldstepclass(params, context);
				}
				if (version == 1.1 && newversion >= 1.1) {
					// 处理没有导入工艺没有创建工序问题
					Newstep.updateoldstepclass(params, context);
				}

				if (version <= 1.32) {// 2020-10-10 change 增加 小于1.32都要更新 1.33
										// 更新生产入库数据不正确问题。
					updateProdstoragedetail(context);
				}

				if (version == 1.66) {
					updateStoreIndetail(context);
				}

			} else {
				message = "当前版本V" + newversion + "早于更新前正常运行版本V" + version + "，更新失败，请还原原有系统文件，或重新更新最新版才可以正常使用系统。";
			}
			System.out.println("updateversion erp end");

		} catch (Exception e) {
			e.printStackTrace();
			message = "系统升级失败，请联系统管理员。系统错误提示： " + e.getMessage().toString();
			try {
				conn.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("message", message);
		return rt;
	}

	// 2020-10-10 1.33版本 更新总库存 生产入库数因作废逻辑不对导致库存不正确问题
	public static void updateProdstoragedetail(ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String message = "";
		System.out.println("updateProdstoragedetail");
		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			Table table = DataUtils
					.queryData(
							conn,
							"select s.* from stock s where round(s.prodstorage_count,6)<>(select round(sum(im.prodstorage_count),6) from itemmonth im where im.companyid=s.companyid and im.itemid=s.itemid and im.houseid=s.houseid and im.batchno=s.batchno)",
							null, null, null, null);
			Iterator<Row> iteratordata = table.getRows().iterator();
			while (iteratordata.hasNext()) {
				Row info = iteratordata.next();
				String companyid = info.getString("companyid");
				String itemid = info.getString("itemid");
				String houseid = info.getString("houseid");
				String batchno = info.getString("batchno");
				double prodstorage_count = Double.parseDouble(info.getValue("prodstorage_count").toString());
				double prodstorage_money = Double.parseDouble(info.getValue("prodstorage_money").toString());

				ps.addBatch("update stock s,s_companyconfig sc,(select sum(im.count) as imcount,sum(im.money) as immoney,sum(im.prodstorage_count) as improdstorage_count,sum(im.prodstorage_money) as improdstorage_money from itemmonth im where im.itemid='"
						+ itemid
						+ "' and im.houseid='"
						+ houseid
						+ "' and im.batchno='"
						+ batchno
						+ "') k set s.count=round(k.imcount,sc.countbit),s.money=round(k.immoney,sc.moneybit),s.prodstorage_count=round(k.improdstorage_count,sc.countbit),s.prodstorage_money=round(k.improdstorage_money,sc.moneybit) where s.itemid='"
						+ itemid + "' and s.houseid='" + houseid + "' and s.batchno='" + batchno + "' and s.companyid=sc.company_id ");
			}

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);

			System.out.println("updateProdstoragedetail end");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				message = message + e.getMessage().toString();
			} catch (Exception e1) {
				// e1.printStackTrace();
				message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
	};

	// 往来应收应付调账作废无效需更新
	public static void changeCustomerBill(ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String message = "";
		System.out.println("changeCustomerBill bein");
		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");

			Table table = DataUtils.queryData(conn, "select c.*,sc.moneybit from customerbill c,s_company_config sc where c.companyid=sc.company_id and status='2' ", null, null, null, null);
			Iterator<Row> iteratordata = table.getRows().iterator();
			while (iteratordata.hasNext()) {
				Row info = iteratordata.next();
				String companyid = info.getString("companyid");
				String sdate = sdfdate.format(info.getDate("operate_time"));
				String customerid = info.getString("customerid");
				double smoney = Double.parseDouble(info.getValue("smoney").toString());
				int stype = info.getInteger("stype");
				int moneybit = info.getInteger("moneybit");

				int syear = Integer.parseInt(sdate.split("-")[0]);
				int smonth = Integer.parseInt(sdate.split("-")[1]);
				sdate = syear + "-" + smonth + "-" + "01";

				if (stype == 1) {
					ps.addBatch("update customer set receivable=round(receivable+" + smoney + "," + moneybit + "),payable=round(payable+" + smoney + "," + moneybit + ") where companyid='" + companyid
							+ "' and customerid='" + customerid + "'");

					ps.addBatch("update customermonth set receivable=round(receivable+" + smoney + "," + moneybit + "),payable=round(payable+" + smoney + "," + moneybit
							+ "),rec_cmoney=round(rec_cmoney-" + smoney + "," + moneybit + "),pay_cmoney=round(pay_cmoney-" + smoney + "," + moneybit + ")" + " where companyid='" + companyid
							+ "' and customerid='" + customerid + "' and sdate='" + sdate + "'");

					ps.addBatch("update customeryear set receivable=round(receivable+" + smoney + "," + moneybit + "),payable=round(payable+" + smoney + "," + moneybit
							+ "),rec_cmoney=round(rec_cmoney-" + smoney + "," + moneybit + "),pay_cmoney=round(pay_cmoney-" + smoney + "," + moneybit + ")" + " where companyid='" + companyid
							+ "' and customerid='" + customerid + "' and syear=" + syear);

				} else if (stype == 2) {
					ps.addBatch("update customer set receivable=round(receivable+" + smoney + "," + moneybit + ")  where companyid='" + companyid + "' and customerid='" + customerid + "'");

					ps.addBatch("update customermonth set receivable=round(receivable+" + smoney + "," + moneybit + "),rec_cmoney=round(rec_cmoney-" + smoney + "," + moneybit + ")"
							+ " where companyid='" + companyid + "' and customerid='" + customerid + "' and sdate='" + sdate + "'");

					ps.addBatch("update customeryear set receivable=round(receivable+" + smoney + "," + moneybit + "),rec_cmoney=round(rec_cmoney-" + smoney + "," + moneybit + ")"
							+ " where companyid='" + companyid + "' and customerid='" + customerid + "' and syear=" + syear);

				} else if (stype == 3) {
					ps.addBatch("update customer set  payable=round(payable+" + smoney + "," + moneybit + ") where companyid='" + companyid + "' and customerid='" + customerid + "'");

					ps.addBatch("update customermonth set  payable=round(payable+" + smoney + "," + moneybit + "),pay_cmoney=round(pay_cmoney-" + smoney + "," + moneybit + ")" + " where companyid='"
							+ companyid + "' and customerid='" + customerid + "' and sdate='" + sdate + "'");

					ps.addBatch("update customeryear set payable=round(payable+" + smoney + "," + moneybit + "),pay_cmoney=round(pay_cmoney-" + smoney + "," + moneybit + ")" + " where companyid='"
							+ companyid + "' and customerid='" + customerid + "' and syear=" + syear);

				}

			}

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);

			System.out.println("changeCustomerBill end");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				message = message + e.getMessage().toString();
			} catch (Exception e1) {
				// e1.printStackTrace();
				message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
	};

	// 修复采购入库金额错误问题
	public static void updateStoreIndetail(ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String message = "";
		System.out.println("updateStoreIndetail");
		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			Table table = DataUtils
					.queryData(
							conn,
							"select sd.*,sc.moneybit,sc.pricebit,round(sd.count*sd.price,sc.moneybit) as newtotal from storeindetail sd,s_company_config sc where sd.create_time>'2022-05-04' and sd.create_time<='2022-05-11' and sd.companyid=sc.company_id and sd.total>round(sd.count*sd.price+1,sc.moneybit) and sd.status=1",
							null, null, null, null);
			Iterator<Row> iteratordata = table.getRows().iterator();

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");

			while (iteratordata.hasNext()) {
				Row info = iteratordata.next();
				String companyid = info.getString("companyid");
				String itemid = info.getString("itemid");
				String houseid = info.getString("houseid");
				String batchno = info.getString("batchno");
				double total = Double.parseDouble(info.getValue("total").toString());
				double newtotal = Double.parseDouble(info.getValue("newtotal").toString());
				Integer moneybit = info.getInteger("moneybit");
				Integer pricebit = info.getInteger("pricebit");
				String detailid = info.getString("detailid");
				String storeinid = info.getString("storeinid");
				String relationdetailid = info.getString("relationdetailid");
				String relationmainid = info.getString("relationmainid");

				String customerid = info.getString("customerid");

				System.out.println(detailid + " " + storeinid + " " + total + " " + newtotal);

				String sdate = sdfdate.format(info.getDate("operate_time"));
				int syear = Integer.parseInt(sdate.split("-")[0]);
				int smonth = Integer.parseInt(sdate.split("-")[1]);
				sdate = syear + "-" + smonth + "-" + "01";

				ps.addBatch("update storeindetail sd set sd.total=" + newtotal + " where detailid='" + detailid + "'");
				ps.addBatch("update storein s set s.total=round((select sum(sd.total) from storeindetail sd where sd.storeinid=s.storeinid)," + moneybit + ") where storeinid='" + storeinid + "'");
				ps.addBatch("update purchaseorderdetail s set s.intotal=round((select sum(sd.total) from storeindetail sd where sd.relationdetailid=s.detailid and sd.status='1')," + moneybit
						+ ") where s.detailid='" + relationdetailid + "'");
				ps.addBatch("update purchaseorder s set s.intotal=round((select sum(sd.total) from storeindetail sd where sd.relationmainid=s.purchaseorderid and sd.status='1')," + moneybit
						+ ") where s.purchaseorderid='" + relationmainid + "'");

				ps.addBatch("update stock s set s.totalmoney=round(s.totalmoney-" + total + "+" + newtotal + "," + moneybit + "),s.money=round(s.money-" + total + "+" + newtotal + "," + moneybit
						+ "),s.newcostprice=if(s.count=0,0,(round(s.money/s.count," + pricebit + "))) where s.itemid='" + itemid + "' and s.houseid='" + houseid + "' and s.batchno='" + batchno + "'");

				ps.addBatch("update ordermonth s set s.purchaseinmoney=round(s.purchaseinmoney-" + total + "+" + newtotal + "," + moneybit + ")  where s.itemid='" + itemid + "' and s.batchno='"
						+ batchno + "' and sdate='" + sdate + "'");

				ps.addBatch("update itemmonth s set s.totalmoney=round(s.totalmoney-" + total + "+" + newtotal + "," + moneybit + "),s.money=round(s.money-" + total + "+" + newtotal + "," + moneybit
						+ ") where s.itemid='" + itemid + "' and s.houseid='" + houseid + "' and s.batchno='" + batchno + "' and sdate='" + sdate + "'");

				String customersql = "update customer set payable=round(payable-" + total + "+" + newtotal + "," + moneybit + ") where customerid='" + customerid + "'";
				ps.addBatch(customersql);

				// 增加往来单位月收支报表
				String customermonthsql = "update customermonth set payable=round(payable-" + total + "+" + newtotal + "," + moneybit + "),pay_purchasein_money=round(pay_purchasein_money-" + total
						+ "+" + newtotal + "," + moneybit + "),pay_add_money=round(pay_add_money-" + total + "+" + newtotal + "," + moneybit + ") where customerid='" + customerid + "' and sdate='"
						+ sdate + "'";
				ps.addBatch(customermonthsql);

				// 增加往来单位年报表
				String customeryearsql = "update customeryear set payable=round(payable-" + total + "+" + newtotal + "," + moneybit + "),pay_purchasein_money=round(pay_purchasein_money-" + total
						+ "+" + newtotal + "," + moneybit + "),pay_add_money=round(pay_add_money-" + total + "+" + newtotal + "," + moneybit + ") where customerid='" + customerid + "' and syear='"
						+ syear + "'";
				ps.addBatch(customeryearsql);

			}

			ps.addBatch("update  storeindetail sd,s_company_config sc set sd.total = round(sd.count*sd.price,sc.moneybit)   where sd.create_time>'2022-05-04' and sd.create_time<='2022-05-11' and sd.companyid=sc.company_id and sd.total>round(sd.count*sd.price+1,sc.moneybit) and sd.status=0");
			ps.addBatch("update storein s,s_company_config sc set s.total=round((select sum(sd.total) from storeindetail sd where sd.storeinid=s.storeinid),sc.moneybit) where s.create_time>'2022-05-04' and s.create_time<='2022-05-11' and s.companyid=sc.company_id and s.status=0 ");

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);

			System.out.println("updateStoreIndetail end");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				message = message + e.getMessage().toString();
			} catch (Exception e1) {
				// e1.printStackTrace();
				message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
	};

	// 修复销售退货相关金额错误问题
	public static void updateStoreOutIndetail(ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String message = "";
		System.out.println("updateStoreOutIndetail");
		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			Table table = DataUtils
					.queryData(
							conn,
							"select sd.*,sc.moneybit,sc.pricebit,round(sd.count*sd.price,sc.moneybit) as newtotal from storeindetail sd,s_company_config sc where sd.create_time>'2022-05-04' and sd.create_time<='2022-05-11' and sd.companyid=sc.company_id and sd.total>round(sd.count*sd.price+1,sc.moneybit) and sd.status=1",
							null, null, null, null);
			Iterator<Row> iteratordata = table.getRows().iterator();

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");

			while (iteratordata.hasNext()) {
				Row info = iteratordata.next();
				String companyid = info.getString("companyid");
				String itemid = info.getString("itemid");
				String houseid = info.getString("houseid");
				String batchno = info.getString("batchno");
				double total = Double.parseDouble(info.getValue("total").toString());
				double newtotal = Double.parseDouble(info.getValue("newtotal").toString());
				Integer moneybit = info.getInteger("moneybit");
				Integer pricebit = info.getInteger("pricebit");
				String detailid = info.getString("detailid");
				String storeinid = info.getString("storeinid");
				String relationdetailid = info.getString("relationdetailid");
				String relationmainid = info.getString("relationmainid");

				String customerid = info.getString("customerid");

				System.out.println(detailid + " " + storeinid + " " + total + " " + newtotal);

				String sdate = sdfdate.format(info.getDate("operate_time"));
				int syear = Integer.parseInt(sdate.split("-")[0]);
				int smonth = Integer.parseInt(sdate.split("-")[1]);
				sdate = syear + "-" + smonth + "-" + "01";

				ps.addBatch("update storeindetail sd set sd.total=" + newtotal + " where detailid='" + detailid + "'");
				ps.addBatch("update storein s set s.total=round((select sum(sd.total) from storeindetail sd where sd.storeinid=s.storeinid)," + moneybit + ") where storeinid='" + storeinid + "'");
				ps.addBatch("update purchaseorderdetail s set s.intotal=round((select sum(sd.total) from storeindetail sd where sd.relationdetailid=s.detailid and sd.status='1')," + moneybit
						+ ") where s.detailid='" + relationdetailid + "'");
				ps.addBatch("update purchaseorder s set s.intotal=round((select sum(sd.total) from storeindetail sd where sd.relationmainid=s.purchaseorderid and sd.status='1')," + moneybit
						+ ") where s.purchaseorderid='" + relationmainid + "'");

				ps.addBatch("update stock s set s.totalmoney=round(s.totalmoney-" + total + "+" + newtotal + "," + moneybit + "),s.money=round(s.money-" + total + "+" + newtotal + "," + moneybit
						+ "),s.newcostprice=if(s.count=0,0,(round(s.money/s.count," + pricebit + "))) where s.itemid='" + itemid + "' and s.houseid='" + houseid + "' and s.batchno='" + batchno + "'");

				ps.addBatch("update ordermonth s set s.purchaseinmoney=round(s.purchaseinmoney-" + total + "+" + newtotal + "," + moneybit + ")  where s.itemid='" + itemid + "' and s.batchno='"
						+ batchno + "' and sdate='" + sdate + "'");

				ps.addBatch("update itemmonth s set s.totalmoney=round(s.totalmoney-" + total + "+" + newtotal + "," + moneybit + "),s.money=round(s.money-" + total + "+" + newtotal + "," + moneybit
						+ ") where s.itemid='" + itemid + "' and s.houseid='" + houseid + "' and s.batchno='" + batchno + "' and sdate='" + sdate + "'");

				String customersql = "update customer set payable=round(payable-" + total + "+" + newtotal + "," + moneybit + ") where customerid='" + customerid + "'";
				ps.addBatch(customersql);

				// 增加往来单位月收支报表
				String customermonthsql = "update customermonth set payable=round(payable-" + total + "+" + newtotal + "," + moneybit + "),pay_purchasein_money=round(pay_purchasein_money-" + total
						+ "+" + newtotal + "," + moneybit + "),pay_add_money=round(pay_add_money-" + total + "+" + newtotal + "," + moneybit + ") where customerid='" + customerid + "' and sdate='"
						+ sdate + "'";
				ps.addBatch(customermonthsql);

				// 增加往来单位年报表
				String customeryearsql = "update customeryear set payable=round(payable-" + total + "+" + newtotal + "," + moneybit + "),pay_purchasein_money=round(pay_purchasein_money-" + total
						+ "+" + newtotal + "," + moneybit + "),pay_add_money=round(pay_add_money-" + total + "+" + newtotal + "," + moneybit + ") where customerid='" + customerid + "' and syear='"
						+ syear + "'";
				ps.addBatch(customeryearsql);

			}

			ps.addBatch("update  storeindetail sd,s_company_config sc set sd.total = round(sd.count*sd.price,sc.moneybit)   where sd.create_time>'2022-05-04' and sd.create_time<='2022-05-11' and sd.companyid=sc.company_id and sd.total>round(sd.count*sd.price+1,sc.moneybit) and sd.status=0");
			ps.addBatch("update storein s,s_company_config sc set s.total=round((select sum(sd.total) from storeindetail sd where sd.storeinid=s.storeinid),sc.moneybit) where s.create_time>'2022-05-04' and s.create_time<='2022-05-11' and s.companyid=sc.company_id and s.status=0 ");

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);

			System.out.println("updateStoreIndetail end");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				message = message + e.getMessage().toString();
			} catch (Exception e1) {
				// e1.printStackTrace();
				message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
	};

}

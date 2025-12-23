<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:m;">  
  <div component="$UI/system/components/justep/model/model" xid="model" onParamsReceive="modelParamsReceive"
    onLoad="modelLoad" style="top:13px;left:130px;height:auto;"> 
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="prodrequisitionData" limit="10" confirmDelete="false" confirmRefresh="false"
      orderBy="md.orderid desc,md.create_time:desc" onAfterRefresh="prodrequisitionDataAfterRefresh"
      idColumn="prodrequisitionid" onBeforeRefresh="prodrequisitionDataBeforeRefresh"
      queryAction="queryProdrequisition_view2" url="/erpscan/save/mainaction" tableName="prodrequisition_view"> 
      <column isCalculate="true" label="操作" name="operate" type="String" xid="xid1"/>  
      <column label="编号" name="prodrequisitionid" type="String" xid="default1"/>  
      <column label="单据类型" name="bill_type" type="String" xid="default7"/>  
      <column label="企业编号" name="companyid" type="String" xid="default8"/>  
      <column label="单据编号" name="orderid" type="String" xid="default9"/>  
      <column label="领用日期" name="operate_time" type="Date" xid="default10"/>  
      <column label="经手人" name="operate_by" type="String" xid="default11"/>  
      <column label="仓库" name="houseid" type="String" xid="default12"/>  
      <column label="领用车间" name="customerid" type="String" xid="default13"/>  
      <column label="数量" name="count" type="Double" xid="default14"/>  
      <column label="总额" name="total" type="Double" xid="default15"/>  
      <column label="状态" name="status" type="String" xid="default16"/>  
      <column label="备注" name="remark" type="String" xid="default17"/>  
      <column label="打印次数" name="printing" type="Integer" xid="default18"/>  
      <column label="导出次数" name="outexcel" type="Integer" xid="default19"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default20"/>  
      <column label="制单人" name="create_by" type="String" xid="default21"/>  
      <column label="制单时间" name="create_time" type="DateTime" xid="default22"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default23"/>  
      <column label="更新人" name="update_by" type="String" xid="default24"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default25"/>  
      <column label="原单号" name="originalbill" type="String" xid="default26"/>  
      <column label="属性列表" name="iproperty" type="String" xid="default27"/>  
      <column label="仓库编号" name="housecode" type="String" xid="default28"/>  
      <column label="仓库" name="housename" type="String" xid="default29"/>  
      <column label="车间编号" name="customercode" type="String" xid="default30"/>  
      <column label="领用部门" name="customername" type="String" xid="default31"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default32"/>  
      <column label="经手人" name="staffname" type="String" xid="default33"/>  
      <column label="工单" name="worksheetid" type="String" xid="xid16"/>  
      <column label="商品ID" name="itemid" type="String" xid="xid17"/>  
      <column label="工单号" name="order_id" type="String" xid="xid19"/>  
      <column label="工单编号" name="billno" type="String" xid="xid20"/>  
      <column label="商品名称" name="itemname" type="String" xid="xid23"/>  
      <column label="补领" name="reworksheet" type="String" xid="xid24"/>  
      <column label="商品规格" name="sformat" type="String" xid="xid26"/>  
      <column label="关联工单编号" name="worksheetbillno" type="String" xid="xid18"/>  
      <column label="关联工单产品ID" name="worksheetitemid" type="String" xid="xid25"/>  
      <column label="关联工单产品批号" name="worksheetbatchno" type="String" xid="xid27"/>  
      <column label="商品编号" name="codeid" type="String" xid="xid28"/> 
      <column label="排产领料单id" name="schedule_pick_id" type="String" xid="spid1"/> 
      <column label="排产领料单号" name="schedule_pick_billno" type="String" xid="spid2"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="prodrequisitiondetailData" limit="20" confirmDelete="false" confirmRefresh="false"
      idColumn="detailid" orderBy="pr.goods_number:asc" onBeforeRefresh="prodrequisitiondetailDataBeforeRefresh"
      onAfterRefresh="prodrequisitiondetailDataAfterRefresh" queryAction="queryProdrequisitiondetail_item_view"
      url="/erpscan/save/detailaction" tableName="prodrequisitiondetail_item_view"> 
      <column label="商品编号" name="codeid" type="String" xid="default34"/>  
      <column label="商品名称" name="itemname" type="String" xid="default35"/>  
      <column label="商品规格" name="sformat" type="String" xid="default36"/>  
      <column label="产品信息" name="pcodeid" type="String" xid="default341"/>  
      <column label="产品名称" name="pitemname" type="String" xid="default352"/>  
      <column label="产品规格" name="psformat" type="String" xid="default363"/>  
      <column label="工单" name="worksheetid" type="String" xid="fxid16"/>  
      <column label="关联工单号" name="worksheetbillno" type="String" xid="fxid18"/>  
      <column label="关联工单产品批号" name="worksheetbatchno" type="String" xid="fxid27"/>  
      <column label="销售订单id" name="salesorderid" type="String" xid="fxid28"/>  
      <column label="销售订单明细id" name="salesorderdetailid" type="String" xid="fxid29"/>  
      <column label="关联订单号" name="order_id" type="String" xid="default365"/>  
      <column label="原订单号" name="soriginalbill" type="String" xid="default367"/>  
      <column label="助记码" name="mcode" type="String" xid="default37"/>  
      <column label="商品分类" name="classid" type="String" xid="default38"/>  
      <column label="单位" name="unit" type="String" xid="default39"/>  
      <column label="图片" name="imgurl" type="String" xid="default40"/>  
      <column label="商品码" name="barcode" type="String" xid="default41"/>  
      <column label="属性1" name="property1" type="String" xid="default42"/>  
      <column label="属性2" name="property2" type="String" xid="default43"/>  
      <column label="属性3" name="property3" type="String" xid="default44"/>  
      <column label="属性4" name="property4" type="String" xid="default45"/>  
      <column label="属性5" name="property5" type="String" xid="default46"/>  
      <column label="商品分类" name="classname" type="String" xid="default47"/>  
      <column label="编号" name="detailid" type="String" xid="default48"/>  
      <column label="主表编号" name="prodrequisitionid" type="String" xid="default49"/>  
      <column label="序号" name="goods_number" type="Integer" xid="default50"/>  
      <column label="企业编号" name="companyid" type="String" xid="default51"/>  
      <column label="业务日期" name="operate_time" type="Date" xid="default52"/>  
      <column label="经手人" name="operate_by" type="String" xid="default53"/>  
      <column label="业务单号" name="orderid" type="String" xid="default54"/>  
      <column label="商品编号" name="itemid" type="String" xid="default55"/>  
      <column label="数量" name="count" type="Double" xid="default60"/>  
      <column label="单价" name="price" type="Double" xid="default61"/>  
      <column label="金额" name="total" type="Double" xid="default62"/>  
      <column label="状态" name="status" type="String" xid="default63"/>  
      <column label="备注" name="remark" type="String" xid="default64"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default65"/>  
      <column label="创建人" name="create_by" type="String" xid="default66"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default67"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default68"/>  
      <column label="更新人" name="update_by" type="String" xid="default69"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default70"/>  
      <column label="原单号" name="originalbill" type="String" xid="default71"/>  
      <column label="批号" name="batchno" type="String" xid="default72"/>  
      <column label="仓库" name="houseid" type="String" xid="xid12"/>  
      <column label="领用部门" name="customerid" type="String" xid="xid13"/>  
      <column label="类型" name="stype" type="String" xid="xid15"/>  
      <column label="仓库编号" name="housecode" type="String" xid="xid21"/>  
      <column label="仓库名称" name="housename" type="String" xid="xid22"/>  
      <column name="unitstate1" type="Integer" xid="oounit1"/>  
      <column name="unitstate2" type="Integer" xid="oounit2"/>  
      <column name="unitstate3" type="Integer" xid="oounit3"/>  
      <column name="unitset1" type="String" xid="oounit4"/>  
      <column name="unitset2" type="String" xid="oounit5"/>  
      <column name="unitset3" type="String" xid="oounit6"/>  
      <column name="counttounit1" type="String" xid="oounit7"/>  
      <column name="counttounit2" type="String" xid="oounit8"/>  
      <column name="counttounit3" type="String" xid="oounit9"/>  
      <column label="工单" name="relationtotalid" type="String" xid="fxid44"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="storehouseData" limit="-1" confirmDelete="false" confirmRefresh="false"
      queryAction="queryStorehouse" saveAction="saveStorehouse" url="/erpscan/pdaaction"
      tableName="storehouse" idColumn="houseid" orderBy="status:asc,defaulthouse:desc,convert(housename using gbk):asc"> 
      <filter name="filter1" xid="filter2">companyid=:companyid</filter>  
      <column label="编号" name="houseid" type="String" xid="default78"/>  
      <column label="企业编号" name="companyid" type="String" xid="default79"/>  
      <column label="仓库编号" name="housecode" type="String" xid="default80"/>  
      <column label="仓库名称" name="housename" type="String" xid="default81"/>  
      <column label="默认仓库" name="defaulthouse" type="Integer" xid="default87"/>  
      <column label="状态" name="status" type="String" xid="default88"/>  
      <column isCalculate="true" label="仓库" name="name" type="String" xid="xid4"/>  
      <rule xid="rule2"> 
        <col name="name" xid="ruleCol2"> 
          <calculate xid="calculate2"> 
            <expr xid="default161">$row.val("housename") +( $row.val("status")=="2"?"-停用":"")</expr> 
          </calculate> 
        </col> 
      </rule> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="statusdata" idColumn="svalue"> 
      <column name="svalue" type="String" xid="column26"/>  
      <column name="slabel" type="String" xid="column27"/>  
      <data xid="default163">[{"svalue":"-1","slabel":"不显示作废"},{"svalue":"0","slabel":"暂存"},{"svalue":"1","slabel":"已记帐"},{"svalue":"2","slabel":"已作废"},{"svalue":"3","slabel":"待出库"}]</data>
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="datetypedata" idColumn="svalue"> 
      <column name="svalue" type="String" xid="xid5"/>  
      <column name="slabel" type="String" xid="xid6"/>  
      <data xid="default162">[{"svalue":"operate_time","slabel":"领用日期"},{"svalue":"create_time","slabel":"制单日期"},{"svalue":"update_time","slabel":"更新日期"}]</data> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="countData" idColumn="firstStatus"> 
      <column label="firstStatus" name="firstStatus" type="Integer" xid="xid2"/>  
      <column label="secondStatus" name="secondStatus" type="Integer" xid="column7"/>  
      <data xid="default159">[{"firstStatus":1,"secondStatus":1}]</data> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="fconditiondata" idColumn="id"> 
      <column name="id" type="String" xid="xid7"/>  
      <column name="houseid" type="String" xid="xid8"/>  
      <column name="housename" type="String" xid="xid9"/>  
      <column name="customid" type="String" xid="xid10"/>  
      <column name="customname" type="String" xid="xid11"/>  
      <column label="更多功能" name="hasmore" type="Integer" xid="xid14"/>  
      <data xid="default164">[{"id":"1","houseid":"","housename":"所有仓库","customid":"","customname":"所有领用部门","hasmore":0}]</data> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="customerData" confirmDelete="false" confirmRefresh="false" limit="-1" orderBy="status:asc,convert(customername using gbk):asc"
      queryAction="queryCustomer" saveAction="saveCustomer" url="/erpscan/pdaaction"
      tableName="customer" idColumn="customerid"> 
      <filter name="filter0" xid="filter1"><![CDATA[companyid=:companyid and role ='4']]></filter>  
      <column label="编号" name="customerid" type="String" xid="default59"/>  
      <column label="企业编号" name="companyid" type="String" xid="default58"/>  
      <column label="客商编号" name="customercode" type="String" xid="default57"/>  
      <column label="客商名称" name="customername" type="String" xid="default56"/>  
      <column label="状态" name="status" type="String" xid="default73"/>  
      <column isCalculate="true" label="客商名称" name="name" type="String" xid="xid3"/>  
      <rule xid="rule1"> 
        <col name="name" xid="ruleCol1"> 
          <calculate xid="calculate1"> 
            <expr xid="default160">$row.val("customername") +( $row.val("status") =="2"?"-停用":"")</expr> 
          </calculate> 
        </col> 
      </rule> 
    </div> 
  </div>  
  <div component="$UI/system/components/justep/controlGroup/controlGroup" class="x-control-group"
    title="生产领用管理" xid="controlGroup1" bind-keypress="controlGroup1Keypress"> 
    <div class="x-control-group-title" xid="controlGroupTitle1"> 
      <span xid="span2">生产领用管理</span> 
    </div>  
    <div component="$UI/system/components/justep/toolBar/toolBar" class="x-toolbar form-inline x-toolbar-spliter"
      xid="toolBar1"> 
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left custombtn"
        label=" 新增" xid="add" onClick="addClick" icon="icon-android-add" style="display:none;"> 
        <i xid="i5" class="icon-android-add"/>  
        <span xid="span12">新增</span> 
      </a>  
      <select component="$UI/system/components/justep/select/select" class="form-control input-sm toolbarbtn"
        xid="statusselect" bind-options="statusdata" bind-optionsValue="svalue" bind-optionsLabel="slabel"
        bind-optionsCaption="全部状态" onChange="statusselectChange"/>  
      <div class="x-gridSelect x-gridSelect-sm selectgridbtn" component="$UI/system/components/justep/gridSelect/gridSelect"
        xid="housegridSelect" inputFilterable="true" bind-ref="$model.fconditiondata.ref(&quot;houseid&quot;)"
        bind-labelRef="$model.fconditiondata.ref(&quot;housename&quot;)" onUpdateValue="housegridSelectUpdateValue"
        autoOptionWidth="false"> 
        <option xid="option1" data="storehouseData" value="houseid" label="name"> 
          <columns> 
            <column name="name" label="仓库名称" xid="xid81"/> 
          </columns> 
        </option> 
      </div>  
      <div class="x-gridSelect x-gridSelect-sm x-edit selectgridbtn" component="$UI/system/components/justep/gridSelect/gridSelect"
        xid="customgridSelect" bind-ref="$model.fconditiondata.ref(&quot;customid&quot;)" inputFilterable="true"
        bind-labelRef="$model.fconditiondata.ref(&quot;customname&quot;)" onUpdateValue="customgridSelectUpdateValue"> 
        <option xid="option2" data="customerData" value="customerid" label="name"> 
          <columns xid="columns2"> 
            <column name="name" label="领用部门" xid="column32"/> 
          </columns> 
        </option> 
      </div>  
      <select component="$UI/system/components/justep/select/select" class="form-control input-sm toolbarbtn"
        xid="datetypeselect" bind-options="datetypedata" bind-optionsValue="svalue"
        bind-optionsLabel="slabel"/>
      <div class="dropdown btn-group" component="$UI/system/components/bootstrap/dropdown/dropdown"
        xid="daydropdown"> 
        <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon dropdown-toggle"
          icon="icon-arrow-down-b" xid="additemTogroupBtn" style="padding:5px 3px;margin-right:2px;margin-bottom:5px;border:1px solid #D2D6DE; color:#4F77AA; "
          bind-visible=" "> 
          <i class="icon-arrow-down-b" xid="i10"/>  
          <span xid="span19"/> 
        </a>  
        <ul component="$UI/system/components/justep/menu/menu" class="x-menu dropdown-menu"
          xid="groupmenu"/> 
      </div>
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn"
        xid="begininput" dataType="Date" placeHolder="开始日期"/>
      <span xid="span20">~</span>
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn"
        xid="endinput" dataType="Date" placeHolder="结束日期"/>
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn"
        xid="iteminput" dataType="String" placeHolder="商品信息,备注"/>
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn"
        xid="billinput" dataType="String" placeHolder="工单号,订单号,产品信息"/>
      <input type="text" class="form-control input-sm toolbarbtn" component="$UI/system/components/justep/input/input"
        xid="searchcontent" placeHolder="单号,经手人,制单人,备注"/>
      <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-icon-left custombtn"
        label=" 查找" xid="findBtn" id="prodrequisitionrefreshBtn" icon="icon-android-search"
        onClick="findBtnClick"> 
        <i xid="i7" class="icon-android-search"/>  
        <span xid="span13">查找</span> 
      </a>  
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left refeshbtn"
        xid="refreshBtn" label="刷新" icon="dataControl dataControl-refresh" onClick="refreshBtnClick"> 
        <i xid="i9" class="dataControl dataControl-refresh"/>  
        <span xid="span11">刷新</span> 
      </a>  
      <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-icon-left custombtn"
        label=" 清除所有条件" xid="clearBtn" onClick="clearBtnClick" icon="dataControl dataControl-clear"> 
        <i xid="i2" class="dataControl dataControl-clear"/>  
        <span xid="span6">清除所有条件</span> 
      </a>
      <div component="$UI/system/components/justep/button/buttonGroup" class="btn-group mygroup"
        tabbed="true" xid="buttonGroup1"> 
        <a component="$UI/system/components/justep/button/button" class="btn btn-link"
          label=" 数据刷新" xid="refreshdatabtn" icon="dataControl dataControl-refresh"
          onClick="refreshdatabtnClick"> 
          <i xid="i8" class="dataControl dataControl-refresh"/>  
          <span xid="span18">数据刷新</span> 
        </a>  
        <a component="$UI/system/components/justep/button/button" class="btn btn-link"
          label="导出模板" xid="infoexample" onClick="infoexampleClick" icon="linear linear-fileempty"
          style="display:none;"> 
          <i xid="i18" class="linear linear-fileempty"/>  
          <span xid="span28">导出模板</span>
        </a>
        <a component="$UI/system/components/justep/button/button" class="btn btn-link"
          label="导入生产领用单" xid="tolist" onClick="tolistClick" icon="linear linear-enterdown"
          style="display:none;"> 
          <i xid="i19" class="linear linear-enterdown"/>  
          <span xid="span27">导入生产领用单</span>
        </a>
        <a component="$UI/system/components/justep/button/button" class="btn btn-link"
          label=" 导出汇总" xid="toexceltotal" onClick="toexceltotalClick" icon="linear linear-exitup"
          style="display:none;"> 
          <i xid="i3" class="linear linear-exitup"/>  
          <span xid="span16">导出汇总</span> 
        </a>  
        <a component="$UI/system/components/justep/button/button" class="btn btn-link"
          label=" 导出明细" xid="toexceldetail" onClick="toexceldetailClick" icon="linear linear-exitup"
          style="display:none;"> 
          <i xid="i4" class="linear linear-exitup"/>  
          <span xid="span16">导出明细</span> 
        </a> 
      </div>
    </div>  
    <div xid="main" bind-visible="$model.countData.val(&quot;secondStatus&quot;) ==1"> 
      <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
        altRows="true" class="x-grid-no-bordered x-grid-title-center gridoverflow"
        xid="grid1" data="prodrequisitionData" multiselect="true" headerMenu="hideColumn,setColumn,groupColumn,saveLayout"
        moveColumn="true" directEdit="false" width="100%" showRowNumber="true" onCellRender="grid1CellRender"
        onRowClick="grid1RowClick" height="300" rowAttr="{style: val(&quot;status&quot;)==2?&quot;color:#AAA&quot;:&quot;color:#555&quot;}"
        useFooter="true" multiboxonly="true"> 
        <columns xid="columns1"> 
          <column name="operate" xid="column25" width="180" align="center"/>  
          <column width="50" name="status" xid="column18" align="center"/>  
          <column width="125" name="orderid" xid="column4" footerData="&quot;合计&quot;" align="center"/>  
          <column width="80" name="operate_time" xid="column5" align="center"/>  
          <column width="70" name="originalbill" xid="column1" align="center"/>  
          <column width="150" name="billno" xid="column16" align="center"/>  
          <column width="150" name="schedule_pick_billno" xid="column40" align="center"></column><column width="100" name="itemname" xid="column37" align="center"/>  
          <column width="80" name="sformat" xid="column38" align="center"/>  
          <column width="100" name="customername" xid="column35" align="center"/>  
          <column width="100" name="count" xid="column15" sorttype="float" footerData="$model.cutZero(($data.sum(&quot;count&quot;)).toFixed($model.countbit))"
            align="center"/>  
          <column width="100" name="total" xid="column13" sorttype="float" align="center"
            footerData="$model.cutZero(($data.sum(&quot;total&quot;)).toFixed($model.moneybit))"/>  
          <column width="100" name="staffname" xid="column34" align="center"/>  
          <column width="150" name="remark" xid="column17" align="center"/>  
          <column width="60" name="printing" xid="column19" align="center"/>  
          <column width="60" name="outexcel" xid="column20" align="center"/>  
          <column width="120" name="create_by" xid="column21" align="center"/>  
          <column width="120" name="create_time" xid="column22" align="center"/>  
          <column width="120" name="update_by" xid="column23" align="center"/>  
          <column width="120" name="update_time" xid="column24" align="center"/> 
  </columns> 
      </div>  
      <div component="$UI/system/components/justep/pagerBar/pagerBar" class="x-pagerbar container-fluid"
        xid="pagerBar1" data="prodrequisitionData" style="margin:0px;"> 
        <div class="row" xid="div1"> 
          <div class="col-sm-3" xid="div2"> 
            <div class="x-pagerbar-length" xid="div3"> 
              <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect"
                class="x-pagerlimitselect" xid="pagerLimitSelect1" defaultValue="50"> 
                <span xid="span3">显示</span>  
                <select component="$UI/system/components/justep/select/select"
                  class="form-control input-sm" xid="select3"> 
                  <option value="10" xid="default2">10</option>  
                  <option value="20" xid="default3">20</option>  
                  <option value="50" xid="default4">50</option>  
                  <option value="100" xid="default5">100</option> 
                </select>  
                <span xid="span4">条</span> 
              </label> 
            </div> 
          </div>  
          <div class="col-sm-3" xid="div4"> 
            <div class="x-pagerbar-info" xid="div5">当前显示0条，共0条</div> 
          </div>  
          <div class="col-sm-6" xid="div6"> 
            <div class="x-pagerbar-pagination" xid="div7"> 
              <ul class="pagination" component="$UI/system/components/bootstrap/pagination/pagination"
                xid="pagination1"> 
                <li class="prev" xid="li2"> 
                  <a href="#" xid="a1"> 
                    <span aria-hidden="true" xid="span5">«</span>  
                    <span class="sr-only" xid="span4">Previous</span> 
                  </a> 
                </li>  
                <li class="next" xid="li3"> 
                  <a href="#" xid="a2"> 
                    <span aria-hidden="true" xid="span7">»</span>  
                    <span class="sr-only" xid="span8">Next</span> 
                  </a> 
                </li> 
              </ul> 
            </div> 
          </div> 
        </div> 
      </div> 
    </div>  
    <div xid="firstToggleDiv" align="center" class="toggle flexdiv" style="margin-bottom:5px;"> 
      <div xid="updiv" class="arrow" bind-click="updivClick"> 
        <span class="arrow-top" xid="span15"/> 
      </div>  
      <div xid="alldiv" class="arrow" bind-click="alldivClick"> 
        <span class="arrow-top" xid="span14"/>  
        <span class="arrow-bottom" xid="span9"/> 
      </div>  
      <div xid="downdiv" class="arrow" bind-click="downdivClick"> 
        <span class="arrow-bottom" xid="span10"/> 
      </div> 
    </div>  
    <div xid="submain" bind-visible="$model.countData.val(&quot;firstStatus&quot;) ==1"> 
      <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
        altRows="true" class="x-grid-no-bordered x-grid-title-center gridoverflow"
        xid="grid2" height="350" data="prodrequisitiondetailData" width="100%" useFooter="true"
        onCellRender="grid2CellRender" serverSort="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout"> 
        <columns xid="columns4"> 
          <column width="40" name="goods_number" xid="column9" align="center"
            sorttype="int"/>  
          <column width="80" name="worksheetbillno" xid="vcolumn1" align="center"/>  
          <column width="140" name="order_id" xid="vcolumn2" align="center"/>  
          <column width="150" name="pcodeid" xid="vcolumn3" sortable="false" align="center"/>  
          <column width="40" name="imgurl" xid="column3" align="center"/>  
          <column width="80" name="codeid" xid="column3" align="center"/>  
          <column width="120" name="barcode" xid="column11" align="center"/>  
          <column width="100" name="itemname" xid="column2" footerData="&quot;合计&quot;"
            align="center"/>  
          <column width="120" name="sformat" xid="column10" align="center"/>  
          <column width="80" name="property1" xid="column30" hidden="true" align="center"/>  
          <column width="80" name="property2" xid="column29" hidden="true" align="center"/>  
          <column width="80" name="property3" xid="column28" hidden="true" align="center"/>  
          <column width="80" name="property4" xid="column11" hidden="true" align="center"/>  
          <column width="80" name="property5" xid="column10" hidden="true" align="center"/>  
          <column width="100" name="batchno" xid="column12" align="center"/>
          <column width="40" name="unit" xid="column8" align="center"/>  
          <column width="80" name="housename" xid="column14" align="center"/>  
          <column width="100" name="count" xid="column6" sorttype="float" footerData="$model.cutZero(($data.sum('count')).toFixed($model.countbit))"
            align="center"/>  
          <column width="80" name="counttounit1" xid="ucol1" align="center" sortable="false"
            hidden="true"/>  
          <column width="80" name="counttounit2" xid="ucol2" align="center" sortable="false"
            hidden="true"/>  
          <column width="80" name="counttounit3" xid="ucol3" align="center" sortable="false"
            hidden="true"/>  
          <column width="100" name="price" xid="column36" sorttype="float" align="center"/>  
          <column width="100" name="total" xid="column39" sorttype="float" align="center"
            footerData="$model.cutZero(($data.sum(&quot;total&quot;)).toFixed($model.moneybit))"/>  
          <column width="150" name="remark" xid="column8" align="center"/>  
          <column width="80" name="classname" xid="column31" align="center"/> 
        </columns> 
      </div> 
    <div component="$UI/system/components/justep/pagerBar/pagerBar" class="x-pagerbar container-fluid" xid="pagerBar2" data="prodrequisitiondetailData">
   <div class="row" xid="div8">
    <div class="col-sm-3" xid="div9">
     <div class="x-pagerbar-length" xid="div10">
      <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect" class="x-pagerlimitselect" xid="pagerLimitSelect2">
       <span xid="span1">显示</span>
       <select component="$UI/system/components/justep/select/select" class="form-control input-sm" xid="select1">
        <option value="10" xid="default6">10</option>
        <option value="20" xid="default74">20</option>
        <option value="50" xid="default75">50</option>
        <option value="100" xid="default76">100</option></select> 
       <span xid="span17">条</span></label> </div> </div> 
    <div class="col-sm-3" xid="div11">
     <div class="x-pagerbar-info" xid="div12">当前显示0条，共0条</div></div> 
    <div class="col-sm-6" xid="div13">
     <div class="x-pagerbar-pagination" xid="div14">
      <ul class="pagination" component="$UI/system/components/bootstrap/pagination/pagination" xid="pagination2">
       <li class="prev" xid="li1">
        <a href="#" xid="a3">
         <span aria-hidden="true" xid="span21">«</span>
         <span class="sr-only" xid="span22">Previous</span></a> </li> 
       <li class="next" xid="li4">
        <a href="#" xid="a4">
         <span aria-hidden="true" xid="span23">»</span>
         <span class="sr-only" xid="span24">Next</span></a> </li> </ul> </div> </div> </div> </div></div> 
  </div>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="windowDialog"/>  
  <span component="$UI/system/components/justep/messageDialog/messageDialog"
    xid="message" width="350"/>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="pringDialog"
    status="normal" width="306mm" height="95%" forceRefreshOnOpen="true"/> 
</div>

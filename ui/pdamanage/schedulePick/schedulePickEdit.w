<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:m;">  
  <div component="$UI/system/components/justep/model/model" xid="model" style="width:201px;height:auto;top:13px;left:67px;"
    onParamsReceive="modelParamsReceive" onLoad="modelLoad"> 
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="storehouseData" idColumn="houseid" limit="-1" confirmDelete="false" confirmRefresh="false"
      orderBy="defaulthouse:desc,convert(housename using gbk):asc"> 
      <column label="编号" name="houseid" type="String" xid="default41"/>  
      <column label="企业编号" name="companyid" type="String" xid="default46"/>  
      <column label="仓库编号" name="housecode" type="String" xid="default47"/>  
      <column label="仓库名称" name="housename" type="String" xid="default48"/>  
      <column label="默认仓库" name="defaulthouse" type="Integer" xid="default54"/>  
      <column label="状态" name="status" type="String" xid="default55"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="data2" idColumn="itemid" limit="9999" onValueChanged="data2ValueChanged"
      confirmRefresh="false" confirmDelete="false" onIndexChanged="data2IndexChanged"> 
      <rule xid="rule3"> 
        <readonly xid="readonly1"> 
          <expr xid="default6">$model.rowReadonly.get()</expr> 
        </readonly>  
        <col name="counttounit1" xid="ruleCol1"> 
          <readonly xid="readonly2"> 
            <expr xid="default109">$row.val("unitstate1") != 1</expr> 
          </readonly> 
        </col>  
        <col name="counttounit2" xid="ruleCol2"> 
          <readonly xid="readonly3"> 
            <expr xid="default110">$row.val("unitstate2") != 1</expr> 
          </readonly> 
        </col>  
        <col name="counttounit3" xid="ruleCol3"> 
          <readonly xid="readonly4"> 
            <expr xid="default111">$row.val("unitstate3") != 1</expr> 
          </readonly> 
        </col>  
        <col name="price" xid="ruleCol4"> 
          <readonly xid="readonly5"> 
            <expr xid="default113">$model.pricereadonly() || $row.val("count") &gt;=0</expr> 
          </readonly> 
        </col>  
        <col name="total" xid="ruleCol6"> 
          <readonly xid="readonly6"> 
            <expr xid="default114">$model.pricereadonly() || $row.val("count") &gt;0</expr> 
          </readonly> 
        </col> 
      </rule>  
      <column label="商品uuid" name="itemid" type="String" xid="xid6"/>  
      <column label="序号" name="goods_number" type="Integer" xid="xid28"/>  
      <column label="图片" name="imgurl" type="String" xid="xid1"/>  
      <column label="商品码" name="barcode" type="String" xid="xid16"/>  
      <column label="商品编号" name="codeid" type="String" xid="xid10"/>  
      <column label="商品名称" name="itemname" type="String" xid="xid11"/>  
      <column label="单价" name="price" type="Double" xid="xid1"/>  
      <column label="商品规格" name="sformat" type="String" xid="xid13"/>  
      <column label="单位" name="unit" type="String" xid="xid20"/>  
      <column label="数量" name="count" type="Double" xid="xid2"/>  
      <column label="金额" name="total" type="Double" xid="xid3"/>  
      <column label="备注" name="remark" type="String" xid="xid5"/>  
      <column label="商品分类" name="classid" type="String" xid="xid15"/>  
      <column label="属性1" name="property1" type="String" xid="xid7"/>  
      <column label="属性2" name="property2" type="String" xid="xid8"/>  
      <column label="属性3" name="property3" type="String" xid="xid9"/>  
      <column label="属性4" name="property4" type="String" xid="xid12"/>  
      <column label="属性5" name="property5" type="String" xid="xid14"/>  
      <column label="商品分类" name="classname" type="String" xid="xid4"/>  
      <column label="最大数量" name="maxcount" type="Double" xid="xid24"/>  
      <column name="detailid" type="String" xid="xid38"/>  
      <column name="create_id" type="String" xid="xid39"/>  
      <column name="create_time" type="DateTime" xid="xid40"/>  
      <column name="create_by" type="String" xid="xid41"/>  
      <column name="unitstate1" type="Integer" xid="unit1"/>  
      <column name="unitstate2" type="Integer" xid="unit2"/>  
      <column name="unitstate3" type="Integer" xid="unit3"/>  
      <column name="unitset1" type="String" xid="unit4"/>  
      <column name="unitset2" type="String" xid="unit5"/>  
      <column name="unitset3" type="String" xid="unit6"/>  
      <column name="counttounit1" type="String" xid="unit7"/>  
      <column name="counttounit2" type="String" xid="unit8"/>  
      <column name="counttounit3" type="String" xid="unit9"/>  
      <column name="original_need" type="Double" xid="xid29"/>  
      <column name="have_picked" type="Double" xid="xid30"/>  
      <column name="still_need" type="Double" xid="xid31"/>  
      <column label="需领/已领/总需领" name="needString" type="String" xid="xid32"/>
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="staffinfoData" idColumn="staffid" limit="-1" orderBy="convert(staffname using gbk):asc"
      confirmDelete="false" confirmRefresh="false"> 
      <column label="编号" name="staffid" type="String" xid="default70"/>  
      <column label="企业编号" name="companyid" type="String" xid="default75"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default76"/>  
      <column label="员工姓名" name="staffname" type="String" xid="default77"/>  
      <column label="状态" name="status" type="String" xid="default102"/>  
      <column label="关联登录帐号" name="userid" type="String" xid="xid26"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="itempropertyData" idColumn="propertyid" limit="-1" orderBy="propertyname:asc,update_by:desc"> 
      <column label="编号" name="propertyid" type="String" xid="default57"/>  
      <column label="企业编号" name="companyid" type="String" xid="default61"/>  
      <column label="属性名" name="propertyname" type="String" xid="default59"/>  
      <column label="属性显示名" name="propertyshow" type="String" xid="default64"/>  
      <column label="属性值" name="propertyvalue" type="String" xid="default63"/>  
      <column label="状态" name="status" type="String" xid="default66"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default65"/>  
      <column label="创建人" name="create_by" type="String" xid="default67"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default58"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default68"/>  
      <column label="更新人" name="update_by" type="String" xid="default60"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default62"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="schedulePickData" idColumn="schedule_pick_id" limit="1"> 
      <rule xid="rule1"> 
        <col name="count" xid="ruleCol8"> 
          <calculate xid="calculate8"> 
            <expr xid="default31">$model.data2.count()&gt;0? ($model.data2.sum("count")).toFixed($model.countbit):0</expr> 
          </calculate> 
        </col> 
      </rule>  
      <column isCalculate="true" label="商品码" name="barcode" type="String" xid="xid18"></column>
  <column isCalculate="true" label="领用部门" name="customername" type="String" xid="xid22"></column>
  <column label="编号" name="schedule_pick_id" type="String" xid="default1"></column>
  <column label="单据类型" name="bill_type" type="String" xid="default2"></column>
  <column label="企业编号" name="companyid" type="String" xid="default3"></column>
  <column label="单据编号" name="orderid" type="String" xid="default4"></column>
  <column label="单据日期" name="operate_time" type="Date" xid="default5"></column>
  <column label="经手人" name="operate_by" type="String" xid="default6"></column>
  <column label="仓库" name="houseid" type="String" xid="default7"></column>
  <column label="单位部门" name="customerid" type="String" xid="default8"></column>
  <column label="数量" name="count" type="Double" xid="default9"></column>
  <column label="总额" name="total" type="Double" xid="default10"></column>
  <column label="状态" name="status" type="String" xid="default11"></column>
  <column label="备注" name="remark" type="String" xid="default12"></column>
  <column label="打印次数" name="printing" type="Integer" xid="default13"></column>
  <column label="导出次数" name="outexcel" type="Integer" xid="default14"></column>
  <column label="创建人ID" name="create_id" type="String" xid="default15"></column>
  <column label="创建人" name="create_by" type="String" xid="default16"></column>
  <column label="创建时间" name="create_time" type="DateTime" xid="default17"></column>
  <column label="更新人ID" name="update_id" type="String" xid="default18"></column>
  <column label="更新人" name="update_by" type="String" xid="default19"></column>
  <column label="更新时间" name="update_time" type="DateTime" xid="default20"></column>
  <column label="原单号" name="originalbill" type="String" xid="default21"></column>
  <column label="属性列表" name="iproperty" type="String" xid="default22"></column>
  <column label="关联排产id" name="relation_schedule_id" type="String" xid="xid33"></column>
  <column label="仓库名" name="housename" type="String" xid="xid34"></column>
  <column label="员工名" name="staffname" type="String" xid="xid35"></column></div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="oldschedulePickData" Column="otherinoutid" limit="1" onBeforeRefresh="oldschedulePickDataBeforeRefresh" idColumn="schedule_pick_id"> 
      <column label="编号" name="schedule_pick_id" type="String" xid="default23"></column>
  <column label="单据类型" name="bill_type" type="String" xid="default24"></column>
  <column label="企业编号" name="companyid" type="String" xid="default25"></column>
  <column label="单据编号" name="orderid" type="String" xid="default26"></column>
  <column label="单据日期" name="operate_time" type="Date" xid="default29"></column>
  <column label="经手人" name="operate_by" type="String" xid="default30"></column>
  <column label="仓库" name="houseid" type="String" xid="default85"></column>
  <column label="单位部门" name="customerid" type="String" xid="default86"></column>
  <column label="数量" name="count" type="Double" xid="default87"></column>
  <column label="总额" name="total" type="Double" xid="default88"></column>
  <column label="状态" name="status" type="String" xid="default89"></column>
  <column label="备注" name="remark" type="String" xid="default91"></column>
  <column label="打印次数" name="printing" type="Integer" xid="default92"></column>
  <column label="导出次数" name="outexcel" type="Integer" xid="default93"></column>
  <column label="创建人ID" name="create_id" type="String" xid="default94"></column>
  <column label="创建人" name="create_by" type="String" xid="default95"></column>
  <column label="创建时间" name="create_time" type="DateTime" xid="default96"></column>
  <column label="更新人ID" name="update_id" type="String" xid="default97"></column>
  <column label="更新人" name="update_by" type="String" xid="default98"></column>
  <column label="更新时间" name="update_time" type="DateTime" xid="default99"></column>
  <column label="原单号" name="originalbill" type="String" xid="default100"></column>
  <column label="属性列表" name="iproperty" type="String" xid="default101"></column>
  <column label="往来单位编号" name="customercode" type="String" xid="default103"></column>
  <column label="往来单位名称" name="customername" type="String" xid="default104"></column>
  <column label="仓库编号" name="housecode" type="String" xid="default105"></column>
  <column label="仓库名称" name="housename" type="String" xid="default106"></column>
  <column label="员工编号" name="staffcode" type="String" xid="default107"></column>
  <column label="员工姓名" name="staffname" type="String" xid="default108"></column>
  <column label="关联排产单" name="relation_schedule_id" type="String" xid="xid25"></column></div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="schedulePickDetailData" limit="999999" confirmDelete="false" confirmRefresh="false"
      idColumn="detailid" orderBy="goods_number:asc" onBeforeRefresh="schedulePickDetailDataBeforeRefresh"> 
      <column label="商品编号" name="codeid" type="String" xid="default168"/>  
      <column label="商品名称" name="itemname" type="String" xid="default169"/>  
      <column label="商品规格" name="sformat" type="String" xid="default170"/>  
      <column label="助记码" name="mcode" type="String" xid="default171"/>  
      <column label="商品分类" name="classid" type="String" xid="default172"/>  
      <column label="单位" name="unit" type="String" xid="default173"/>  
      <column label="图片" name="imgurl" type="String" xid="default174"/>  
      <column label="商品码" name="barcode" type="String" xid="default175"/>  
      <column label="属性1" name="property1" type="String" xid="default176"/>  
      <column label="属性2" name="property2" type="String" xid="default177"/>  
      <column label="属性3" name="property3" type="String" xid="default178"/>  
      <column label="属性4" name="property4" type="String" xid="default179"/>  
      <column label="属性5" name="property5" type="String" xid="default180"/>  
      <column label="classname" name="classname" type="String" xid="default181"/>  
      <column label="编号" name="detailid" type="String" xid="default182"/>  
      <column label="主表编号" name="schedule_pick_id" type="String" xid="default183"/>  
      <column label="序号" name="goods_number" type="Integer" xid="default184"/>  
      <column label="企业编号" name="companyid" type="String" xid="default185"/>  
      <column label="单据日期" name="operate_time" type="Date" xid="default186"/>  
      <column label="经手人" name="operate_by" type="String" xid="default187"/>  
      <column label="单据编号" name="orderid" type="String" xid="default188"/>  
      <column label="商品编号" name="itemid" type="String" xid="default189"/>  
      <column label="仓库" name="houseid" type="String" xid="default190"/>  
      <column label="单位部门" name="customerid" type="String" xid="default191"/>  
      <column label="类型" name="stype" type="String" xid="default192"/>  
      <column label="数量" name="count" type="Double" xid="default193"/>  
      <column label="单价" name="price" type="Double" xid="default194"/>  
      <column label="金额" name="total" type="Double" xid="default195"/>  
      <column label="状态" name="status" type="String" xid="default196"/>  
      <column label="备注" name="remark" type="String" xid="default197"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default198"/>  
      <column label="创建人" name="create_by" type="String" xid="default199"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default200"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default201"/>  
      <column label="更新人" name="update_by" type="String" xid="default202"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default203"/>  
      <column label="原单号" name="originalbill" type="String" xid="default204"/>  
      <column label="批号" name="batchno" type="String" xid="default205"/>  
      <column name="unitstate1" type="Integer" xid="pdunit1"/>  
      <column name="unitstate2" type="Integer" xid="pdunit2"/>  
      <column name="unitstate3" type="Integer" xid="pdunit3"/>  
      <column name="unitset1" type="String" xid="pdunit4"/>  
      <column name="unitset2" type="String" xid="pdunit5"/>  
      <column name="unitset3" type="String" xid="pdunit6"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="iteminfoData" limit="-1" confirmDelete="false" confirmRefresh="false" queryAction="queryItem_batchno_stock"
      url="/erpscan/pdaaction" tableName="item_batchno_stock" idColumn="goods_number"
      orderBy="batchno:asc"> 
      <column label="编号" name="itemid" type="String" xid="default32"/>  
      <column label="企业编号" name="companyid" type="String" xid="default33"/>  
      <column label="商品编号" name="codeid" type="String" xid="default34"/>  
      <column label="商品名称" name="itemname" type="String" xid="default35"/>  
      <column label="商品规格" name="sformat" type="String" xid="default36"/>  
      <column label="助记码" name="mcode" type="String" xid="default37"/>  
      <column label="商品分类" name="classid" type="String" xid="default38"/>  
      <column label="单位" name="unit" type="String" xid="default39"/>  
      <column label="图片地址" name="imgurl" type="String" xid="default40"/>  
      <column label="进单价" name="inprice" type="Double" xid="default42"/>  
      <column label="零售价" name="outprice" type="Double" xid="default43"/>  
      <column label="商品码" name="barcode" type="String" xid="default44"/>  
      <column label="备注" name="remark" type="String" xid="default45"/>  
      <column label="状态" name="status" type="String" xid="default49"/>  
      <column label="属性1" name="property1" type="String" xid="default50"/>  
      <column label="属性2" name="property2" type="String" xid="default51"/>  
      <column label="属性3" name="property3" type="String" xid="default52"/>  
      <column label="属性4" name="property4" type="String" xid="default53"/>  
      <column label="属性5" name="property5" type="String" xid="default56"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default69"/>  
      <column label="创建人" name="create_by" type="String" xid="default71"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default72"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default73"/>  
      <column label="更新人" name="update_by" type="String" xid="default74"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default78"/>  
      <column label="一级销售单价" name="outprice1" type="Double" xid="default79"/>  
      <column label="二级销售单价" name="outprice2" type="Double" xid="default80"/>  
      <column label="三级销售单价" name="outprice3" type="Double" xid="default81"/>  
      <column label="四级销售单价" name="outprice4" type="Double" xid="default82"/>  
      <column label="五级销售单价" name="outprice5" type="Double" xid="default83"/>  
      <column label="仓库" name="houseid" type="String" xid="default84"/>  
      <column label="batchno" name="batchno" type="String" xid="default90"/>  
      <column label="count" name="count" type="Double" xid="default123"/>  
      <column label="money" name="money" type="Double" xid="default124"/>  
      <column label="newcostprice" name="newcostprice" type="Double" xid="default28"/>  
      <column isCalculate="true" label="序号" name="goods_number" type="String"
        xid="xid23"/>  
      <column name="unitstate1" type="Integer" xid="iunit1"/>  
      <column name="unitstate2" type="Integer" xid="iunit2"/>  
      <column name="unitstate3" type="Integer" xid="iunit3"/>  
      <column name="unitset1" type="String" xid="iunit4"/>  
      <column name="unitset2" type="String" xid="iunit5"/>  
      <column name="unitset3" type="String" xid="iunit6"/>  
      <rule xid="rule2"> 
        <col name="goods_number" xid="ruleCol5"> 
          <calculate xid="calculate5"> 
            <expr xid="default27">$row.index()+1</expr> 
          </calculate> 
        </col> 
      </rule>  
      <column label="checkout_count" name="checkout_count" type="Double" xid="xid17"/>  
      <column label="classname" name="classname" type="String" xid="xid19"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="prodstoragedetailData" limit="999999" confirmDelete="false" confirmRefresh="false"
      queryAction="queryProdstoragedetail_item_view" url="/erpscan/save/detailaction"
      tableName="prodstoragedetail_item_view" idColumn="detailid" orderBy="goods_number:asc"> 
      <column label="编号" name="detailid" type="String" xid="default118"/>  
      <column label="主表编号" name="prodstorageid" type="String" xid="default117"/>  
      <column label="序号" name="goods_number" type="Integer" xid="default116"/>  
      <column label="企业编号" name="companyid" type="String" xid="default115"/>  
      <column label="单据编号" name="orderid" type="String" xid="default112"/>  
      <column label="商品编号" name="itemid" type="String" xid="default153"/>  
      <column label="商品名称" name="itemname" type="String" xid="fdefault153"/>  
      <column label="仓库" name="houseid" type="String" xid="default152"/>  
      <column label="数量" name="count" type="Double" xid="fdefault47"/>  
      <column label="备注" name="remark" type="String" xid="default141"/>  
      <column label="批号" name="batchno" type="String" xid="default126"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="data1" idColumn="id"> 
      <rule xid="rule5"> 
        <col name="id" xid="ruleCol9"> 
          <calculate xid="calculate2"> 
            <expr xid="default120">$row.index()+1</expr>
          </calculate> 
        </col> 
      </rule>  
      <column name="id" type="String" xid="column24"/>  
      <column name="itemid" type="String" xid="column25"/>  
      <column name="houseid" type="String" xid="column26"/>  
      <column name="batchno" type="String" xid="xid74"/>  
      <column name="maxcount" type="Double" xid="xid75"/>  
      <column name="totalcount" type="Double" xid="xid76"/>  
      <column name="codeid" type="String" xid="xid77"/>  
      <column name="itemname" type="String" xid="xid78"/>  
      <column name="housename" type="String" xid="xid79"/>
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="scheduleData" confirmRefresh="false" confirmDelete="false"/>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="itemBatchConfigData" confirmDelete="false" confirmRefresh="false" limit="9999"
      idColumn="itemid" onBeforeRefresh="itemBatchConfigDataBeforeRefresh">
      <column label="商品id" name="itemid" type="String" xid="xid21"/>  
      <column label="批号配置" name="config" type="Object" xid="xid27"/>
    </div>
  </div>  
  <div component="$UI/system/components/justep/controlGroup/controlGroup" class="x-control-group gridoverflow"
    xid="controlGroup1"> 
    <div xid="div6"> 
      <div component="$UI/system/components/bootstrap/row/row" class="row"
        xid="row4"> 
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col11"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit6"> 
            <div class="x-label titlespan" style="font-weight:bold;width:100%"
              align="left" xid="div9">排产领料单</div> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col13"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit10"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div1">单据编号：</div>  
            <input component="$UI/system/components/justep/input/input" class="form-control input-sm x-edit input-frame"
              xid="input4" dataType="Date" bind-ref="$model.schedulePickData.ref(&quot;orderid&quot;)"
              disabled="true" style="font-size:14px;font-weight:bold;background-color:white;border:0;"/> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col14"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit4"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div2"> 
              <span class="redCss" xid="span5">*</span>出库日期：
            </div>  
            <input component="$UI/system/components/justep/input/input" class="form-control input-sm x-edit input-frame"
              xid="input1" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.schedulePickData.ref(&quot;operate_time&quot;)"
              onChange="input1Change"/> 
          </div> 
        </div> 
      </div>  
      <div component="$UI/system/components/bootstrap/row/row" class="row"
        xid="row2"> 
        <div class="col col-xs-4" xid="col3">
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit11"> 
            <label class="x-label" xid="label1" style="font-weight:bold;width:90px;"><![CDATA[排产单：]]></label>  
            <div class="x-edit" xid="div14" bind-click="div14Click" style="input-frame">
              <input component="$UI/system/components/justep/input/input" class="form-control  input-sm customercss"
                xid="input5" placeHolder="请先选择排产单" style="background-color:white;"/>
            </div>
          </div>
        </div>  
        <div class="col col-xs-4" xid="col4"/>  
        <div class="col col-xs-4" xid="col5"/>
      </div>
    </div>  
    <div xid="div8" class="baseCss"> 
      <div component="$UI/system/components/bootstrap/row/row" class="row"
        xid="row7"> 
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col24"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit2"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div5"> 
              <span class="redCss" xid="span8">*</span>单位部门：
            </div>  
            <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon dropdown-toggle oprbtn"
              icon="dataControl dataControl-plusl" xid="addcustomerbutton" style="display:none;padding:5px 8px;color:#fff;background-color:#4F77AA;"
              bind-visible=" " onClick="addcustomerbuttonClick"> 
              <i class="dataControl dataControl-plusl" xid="i14"/>  
              <span xid="span15"/> 
            </a>  
            <div xid="customerdiv" class="x-edit" bind-click="customerdivClick"> 
              <input component="$UI/system/components/justep/input/input" class="form-control  input-sm customercss input-frame"
                xid="customerinput" style="background-color:white;" bind-ref="$model.schedulePickData.ref(&quot;customername&quot;)"
                placeHolder="点击输入框选择单位部门"/> 
            </div>  
            <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon dropdown-toggle oprbtn"
              icon="glyphicon glyphicon-option-horizontal" xid="cselectbtn" style="padding:5px 8px;color:#fff;background-color:#4F77AA;"
              bind-visible=" " onClick="customerdivClick"> 
              <i class="glyphicon glyphicon-option-horizontal" xid="i17"/>  
              <span xid="span10"/> 
            </a> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col25"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit3"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div3"> 
              <span class="redCss" xid="span6">*</span>仓库：
            </div>  
            <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon dropdown-toggle oprbtn"
              icon="dataControl dataControl-plusl" xid="addhousebutton" style="display:none;padding:5px 8px;color:#fff;background-color:#4F77AA;"
              bind-visible=" " onClick="addhousebuttonClick"> 
              <i class="dataControl dataControl-plusl" xid="i11"/>  
              <span xid="span13"/> 
            </a>  
            <div class="x-gridSelect x-gridSelect-sm x-edit" component="$UI/system/components/justep/gridSelect/gridSelect"
              xid="gridSelect2" bind-ref="$model.schedulePickData.ref(&quot;houseid&quot;)"
              inputFilterable="true" bind-labelRef='$model.storehouseData.ref("housename")'> 
              <option xid="option3" data="storehouseData" value="houseid" label="housename"> 
                <columns> 
                  <column name="housename" label="仓库名称"/>  
                  <column align="right" name="housecode" label="仓库编号"/> 
                </columns> 
              </option> 
            </div> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col26"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit5"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div4"> 
              <span class="redCss" xid="span7">*</span>经手人：
            </div>  
            <div class="dropdown btn-group dropdownhide oprbtn" component="$UI/system/components/bootstrap/dropdown/dropdown"
              xid="staffdropdown"> 
              <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon dropdown-toggle"
                icon="icon-arrow-down-b" xid="togroupBtn1" style="padding:5px 10px;color:#fff;background-color:#4F77AA;"
                bind-visible=" "> 
                <i class="icon-arrow-down-b" xid="i10"/>  
                <span xid="span19"/> 
              </a>  
              <ul component="$UI/system/components/justep/menu/menu" class="x-menu dropdown-menu"
                xid="groupmenu"> 
                <li class="x-menu-item" xid="item1"> 
                  <a component="$UI/system/components/justep/button/button"
                    class="btn btn-link" label="新增" xid="addbutton" onClick="addbuttonClick"> 
                    <i xid="i8"/>  
                    <span xid="span12">新增</span> 
                  </a> 
                </li>  
                <li class="x-menu-item" xid="item2"> 
                  <a component="$UI/system/components/justep/button/button"
                    class="btn btn-link" label="刷新" xid="refreshbutton" onClick="refreshbuttonClick"> 
                    <i xid="i9"/>  
                    <span xid="span12">刷新</span> 
                  </a> 
                </li> 
              </ul> 
            </div>  
            <div class="x-gridSelect x-gridSelect-sm x-edit" component="$UI/system/components/justep/gridSelect/gridSelect"
              xid="staffgs" clearButton="false" inputChangeable="false" bind-ref="$model.schedulePickData.ref(&quot;operate_by&quot;)"
              inputFilterable="true" bind-labelRef='$model.schedulePickData.ref("staffname")'> 
              <option xid="option2" data="staffinfoData" value="staffid" label="staffname"> 
                <columns> 
                  <column name="staffname" label="员工姓名"/>  
                  <column align="right" name="staffcode" label="员工编号"/> 
                </columns> 
              </option> 
            </div> 
          </div> 
        </div> 
      </div>  
      <div component="$UI/system/components/bootstrap/row/row" class="row"
        xid="row5"> 
        <div class="col col-xs-12 col-sm-3 col-md-4" xid="col17"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit1"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div7">原单号：</div>  
            <input component="$UI/system/components/justep/input/input" class="form-control input-sm x-edit input-frame"
              xid="input3" dataType="Date" bind-ref="$model.schedulePickData.ref(&quot;originalbill&quot;)"
              maxLength="50"/> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-8" xid="col18"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit7"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div10">备注：</div>  
            <input component="$UI/system/components/justep/input/input" class="form-control input-sm x-edit input-frame"
              xid="input2" bind-ref="$model.schedulePickData.ref(&quot;remark&quot;)" maxLength="200"/> 
          </div> 
        </div> 
      </div>  
      <div component="$UI/system/components/bootstrap/row/row" class="row"
        xid="row6"> 
        <div class="col col-xs-12 col-sm-6 col-md-4" xid="col22"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="labelEdit8" style="display:none;"> 
            <div class="x-label" style="font-weight:bold;width:90px;" align="left"
              xid="div11">商品码：</div>  
            <input component="$UI/system/components/justep/input/input" class="form-control input-sm x-edit"
              xid="barcodeinput" bind-focus="barcodeinputFocus" dataType="String"
              maxLength="100" bind-ref="$model.schedulePickData.ref(&quot;barcode&quot;)" bind-keypress="barcodeinputKeypress"
              placeHolder="录入商品码后按&lt;回车&gt;键"/> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-8" xid="col15"> 
          <div component="$UI/system/components/bootstrap/row/row" class="row"
            xid="row1"> 
            <div class="col col-xs-12 col-sm-6 col-md-6  flexdivleft" xid="col1"> 
              <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left itemaddbtn oprbtn"
                label=" 选择商品" xid="itemselect" onClick="itemselectbtn" icon="icon-android-add"> 
                <i xid="i5" class="icon-android-add"/>  
                <span xid="span1">选择商品</span> 
              </a>  
              <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon findbtn oprbtn"
                icon="dataControl dataControl-plusl" xid="itemaddbutton" bind-visible=" "
                onClick="itemaddbuttonClick" style="display:none;padding-left:8px;padding-right:8px;"> 
                <i class="dataControl dataControl-plusl" xid="i15"/>  
                <span xid="span16"/> 
              </a>  
              <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon findbtn"
                icon="e-commerce e-commerce-saomiao" xid="scanbtn" bind-visible=" "
                onClick="scanbtnClick" style="padding:2px 10px;font-size:16px;display:none;"> 
                <i class="e-commerce e-commerce-saomiao" xid="i12"/>  
                <span xid="span17"/> 
              </a> 
            </div>  
            <div class="col col-xs-12 col-sm-6 col-md-6 flexdiv" xid="col2"> 
              <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left findbtn oprbtn"
                label=" 保存" xid="submitbtn" onClick="submitbtnClick" icon="icon-android-checkmark"
                style="display:none;"> 
                <i xid="i1" class="icon-android-checkmark"/>  
                <span xid="span4">保存</span> 
              </a>  
              <div class="dropdown btn-group" component="$UI/system/components/bootstrap/dropdown/dropdown"
                xid="actsavedropdown" bind-visible=" "> 
                <a component="$UI/system/components/justep/button/button"
                  class="btn  btn-sm btn-icon-right dropdown-toggle" label="保存其他方式"
                  icon="icon-arrow-down-b" xid="actsavelistbtn" style="display:none;color:#fff;background-color:#4F77AA;margin-right:1px;margin-bottom:4px;"> 
                  <i class="icon-arrow-down-b" xid="i25"/>  
                  <span xid="span3">保存其他方式</span> 
                </a>  
                <ul component="$UI/system/components/justep/menu/menu" class="x-menu dropdown-menu"
                  xid="actsavemenu"> 
                  <li class="x-menu-item" xid="actsaveitem1"> 
                    <a component="$UI/system/components/justep/button/button"
                      class="btn  btn-sm btn-icon-left findbtn" label="保存并新增" xid="actsaveaddBtn"
                      onClick="actsaveaddBtnClick" icon="icon-android-checkmark"> 
                      <i xid="i23" class="icon-android-checkmark"/>  
                      <span xid="span2">保存并新增</span> 
                    </a> 
                  </li>  
                  <li class="x-menu-item" xid="actsaveitem2"> 
                    <a component="$UI/system/components/justep/button/button"
                      class="btn  btn-sm btn-icon-left findbtn" label="保存并复制" xid="actsavecopyBtn"
                      onClick="actsavecopyBtnClick" icon="icon-android-checkmark"> 
                      <i xid="i24" class="icon-android-checkmark"/>  
                      <span xid="span22">保存并复制</span> 
                    </a> 
                  </li> 
                </ul> 
              </div>  
              <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left findbtn oprbtn"
                label="暂存" onClick="tempstorageBtnClick" icon="icon-android-checkmark"
                xid="tempstorageBtn"> 
                <i xid="i4" class="icon-android-checkmark"/>  
                <span xid="span9">暂存</span> 
              </a>  
              <div class="dropdown btn-group" component="$UI/system/components/bootstrap/dropdown/dropdown"
                xid="tempsavedropdown" bind-visible=" "> 
                <a component="$UI/system/components/justep/button/button"
                  class="btn  btn-sm btn-icon-right dropdown-toggle" label="暂存其他方式"
                  icon="icon-arrow-down-b" xid="tempsavedlistbtn" style="color:#fff;background-color:#4F77AA;margin-right:1px;margin-bottom:4px; display:none;"> 
                  <i class="icon-arrow-down-b" xid="i16"/>  
                  <span xid="span26">暂存其他方式</span> 
                </a>  
                <ul component="$UI/system/components/justep/menu/menu" class="x-menu dropdown-menu"
                  xid="tempsavedmenu"> 
                  <li class="x-menu-item" xid="tempsaveditem1"> 
                    <a component="$UI/system/components/justep/button/button"
                      class="btn  btn-sm btn-icon-left findbtn" label="暂存并新增" xid="saveaddBtn"
                      onClick="saveaddBtnClick" icon="icon-android-checkmark"> 
                      <i xid="i2" class="icon-android-checkmark"/>  
                      <span xid="span24">暂存并新增</span> 
                    </a> 
                  </li>  
                  <li class="x-menu-item" xid="tempsaveditem2"> 
                    <a component="$UI/system/components/justep/button/button"
                      class="btn  btn-sm btn-icon-left findbtn" label="暂存并复制" xid="savecopyBtn"
                      onClick="savecopyBtnClick" icon="icon-android-checkmark"> 
                      <i xid="i3" class="icon-android-checkmark"/>  
                      <span xid="span25">暂存并复制</span> 
                    </a> 
                  </li> 
                </ul> 
              </div> 
            </div> 
          </div> 
        </div> 
      </div> 
    </div>  
    <div xid="div12" style="padding:5px 5px;"> 
      <span xid="span14" style="color:red;">注意：【暂存】单据可删除修改，不影响库存，但一旦【保存】单据不能修改删除，只能作废，影响库存数。</span>  
      <div component="$UI/system/components/bootstrap/row/row" class="row"
        xid="row8"> 
        <div class="col col-xs-12 col-sm-6 col-md-6" xid="col16"> 
          <div xid="div25" class="flexdivleft" style="margin-top:5px;"> 
            <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left findbtn oprbtn"
              label="删除" xid="deletebtn" onClick="deletebtnClick" icon="icon-android-remove"> 
              <i xid="i6" class="icon-android-remove"/>  
              <span xid="span18">删除</span> 
            </a>  
            <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left findbtn oprbtn"
              label="上移" onClick="upClick" icon="icon-arrow-up-b" xid="upclick"> 
              <i xid="i19" class="icon-arrow-up-b"/>  
              <span xid="span27">上移</span> 
            </a>  
            <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left findbtn oprbtn"
              label="下移" onClick="downClick" icon="icon-arrow-down-b" xid="downclick"
              style="margin-right:3px;"> 
              <i xid="i20" class="icon-arrow-down-b"/>  
              <span xid="span28">下移</span> 
            </a> 
          </div> 
        </div>  
        <div class="col col-xs-12 col-sm-6 col-md-6" xid="col9"></div> 
      </div> 
    </div>  
    <div xid="contentDiv" class="contentDiv"><div xid="mainDiv" class="mainDiv"><div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true" altRows="false" class="x-grid-title-center gridoverflow" xid="grid2" height="auto" data="data2" width="100%" useFooter="true" onCellRender="grid2CellRender" serverSort="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout" multiselect="true" multiboxonly="true" onReload="grid2Reload"> 
      <columns xid="columns4"> 
        <column width="40" name="goods_number" xid="column18" align="center" sortable="false" />  
        <column width="100" name="itemid" xid="column9" label="操作" sortable="false" align="center" />  
        <column width="40" name="imgurl" xid="column3" align="center" sortable="false" />  
        <column width="80" name="codeid" xid="column1" sortable="false" align="center" />  
        <column width="120" name="barcode" xid="column11" sortable="false" align="center" />  
        <column width="100" name="itemname" xid="column2" sortable="false" footerData="&quot;合计&quot;" align="center" />  
        <column width="100" name="sformat" xid="column10" align="center" sortable="false" />  
          
          
          
          
          
        <column width="40" name="unit" xid="column4" sortable="false" align="center" />  
        <column width="120" name="count" xid="column6" footerData="($data.sum('count')).toFixed($model.countbit)" editable="false" sortable="false" editor="input" align="center" />  
        <column width="120" name="needString" xid="column7" align="center" />
        <column width="80" name="property1" xid="column13" hidden="true" align="center" sortable="false" /><column width="80" name="property2" xid="column14" hidden="true" align="center" sortable="false" /><column width="80" name="property3" xid="column15" hidden="true" align="center" sortable="false" /><column width="80" name="property4" xid="column16" hidden="true" align="center" sortable="false" /><column width="80" name="property5" xid="column17" hidden="true" align="center" sortable="false" /><column width="100" hidden="true" name="counttounit1" xid="ucol1" align="center" sortable="false" editable="true" editor="component" disableEditorDisplay="false"> 
          <editor xid="uu_editor1"> 
            <input bind-ref="ref('counttounit1')" class="form-control x-edit-focusin" component="$UI/system/components/justep/input/input" xid="countinputdata21" onFocus="unitinputFocus" valueUpdateMode="input" onBlur="unitinputBlur" /> 
          </editor> 
        </column>  
        <column width="100" hidden="true" name="counttounit2" xid="ucol2" sortable="false" align="center" editable="true" editor="component" disableEditorDisplay="false"> 
          <editor xid="uu_editor2"> 
            <input bind-ref="ref('counttounit2')" class="form-control x-edit-focusin" component="$UI/system/components/justep/input/input" xid="countinputdata22" onFocus="unitinputFocus" onBlur="unitinputBlur" /> 
          </editor> 
        </column>  
        <column width="100" hidden="true" name="counttounit3" xid="ucol3" sortable="false" align="center" editable="true" editor="component" disableEditorDisplay="false"> 
          <editor xid="uu_editor3"> 
            <input bind-ref="ref('counttounit3')" class="form-control x-edit-focusin" component="$UI/system/components/justep/input/input" xid="countinputdata23" onFocus="unitinputFocus" onBlur="unitinputBlur" /> 
          </editor> 
        </column>  
        <column width="120" name="price" multiRowEditor="false" disableEditorDisplay="false" editable="true" editor="component" xid="column5" align="center" sortable="false"> 
          <editor xid="editor3"> 
            <div class="input-group" component="$UI/system/components/bootstrap/inputGroup/inputGroup" xid="inputGroup1"> 
              <input type="text" class="form-control x-edit-focusin" component="$UI/system/components/justep/input/input" bind-ref="ref('price')" xid="priceinputdata2" />  
              <div class="input-group-btn" xid="div21"> 
                <a component="$UI/system/components/justep/button/button" class="btn btn-default" xid="pricebutton" onClick="pricebuttonClick" label="..."> 
                  <i xid="i21" />  
                  <span xid="span26">...</span> 
                </a> 
              </div> 
            </div> 
          </editor> 
        </column>  
        <column width="120" name="total" xid="total0" align="center" sortable="false" editable="true" editor="component" disableEditorDisplay="false" footerData="parseFloat(($data.sum('total')).toFixed($model.moneybit))"> 
          <editor xid="taxuu0"> 
            <input bind-ref="ref('total')" class="form-control x-edit-focusin" component="$UI/system/components/justep/input/input" xid="totalinputdata2" valueUpdateMode="input" /> 
          </editor> 
        </column>  
        <column width="140" name="remark" editable="true" editor="input" xid="column8" sortable="false" align="center" />  
        <column width="100" name="classname" xid="column12" align="center" sortable="false" /> 
      </columns> 
    </div></div>
  <div xid="editDiv" class="editDiv"></div></div> 
  </div>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="itemDialog"
    status="normal" forceRefreshOnOpen="true" height="98%"
    width="98%" onReceive="itemDialogReceive"/>  
  <span component="$UI/system/components/justep/messageDialog/messageDialog"
    xid="message" width="320" title="信息提示"/>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="windowDialog"
    status="normal" width="500px" height="620px" title="编辑仓库" showTitle="true"/>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="workshopDialog"
    status="normal" width="500px" height="300px" title="编辑车间" showTitle="true"/>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="customerDialog"
    status="normal" forceRefreshOnOpen="true" onReceived="customerDialogReceived"
    height="90%"/>  
  <span component="$UI/system/components/justep/messageDialog/messageDialog"
    xid="printMessage" width="320" title="信息提示" message="已保存成功，是否要进行打印？" type="YesNo"
    onYes="printMessageYes" onNo="printMessageNo"/>  
  <a component="$UI/system/components/justep/button/button" class="btn btn-default"
    xid="selectoldpricereturnbtn" id="selectoldpricereturn4" style="display:none;"
    onClick="selectoldpricereturnbtnClick"> 
    <i xid="i22"/>  
    <span xid="span29"/> 
  </a>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="scheduleDialog"
    width="98%" height="98%" title="选择排产单" onReceive="scheduleDialogReceive"/>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="itemConfigDialog"
    onReceive="itemConfigDialogReceive"/>
</div>

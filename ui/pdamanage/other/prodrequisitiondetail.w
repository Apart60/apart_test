<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:m;">  
  <div component="$UI/system/components/justep/model/model" xid="model" onParamsReceive="modelParamsReceive"
    onLoad="modelLoad" style="width:164px;top:9px;left:115px;height:auto;"> 
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="prodrequisitionData" limit="1" confirmDelete="false" confirmRefresh="false"
      orderBy="create_time:desc" idColumn="prodrequisitionid" queryAction="queryProdrequisition_view"
      url="/erpscan/erpaction" tableName="prodrequisition_view"> 
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
      <column label="仓库名称" name="housename" type="String" xid="default29"/>  
      <column label="部门编号" name="customercode" type="String" xid="default30"/>  
      <column label="领用部门" name="customername" type="String" xid="default31"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default32"/>  
      <column label="经手人" name="staffname" type="String" xid="default33"/>  
      <column label="工单" name="worksheetid" type="String" xid="xid1"/>  
      <column label="工单领用数量" name="worksheetcount" type="Double" xid="xid2"/>  
      <column label="订单编号" name="order_id" type="String" xid="xid3"/>  
      <column label="工单编号" name="billno" type="String" xid="xid5"/>  
      <column label="商品名称" name="itemname" type="String" xid="xid6"/>  
      <column label="补领" name="reworksheet" type="String" xid="xid7"/>  
      <column label="商品ID" name="itemid" type="String" xid="xid8"/>  
      <column label="商品规格" name="sformat" type="String" xid="xid10"/>  
      <column label="商品编号" name="codeid" type="String" xid="xid9"/>
      <column label="排产领料单id" name="schedule_pick_id" type="String" xid="spid1"/> 
      <column label="排产领料单号" name="schedule_pick_billno" type="String" xid="spid2"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false" xid="prodrequisitiondetailData" limit="999999" confirmDelete="false" confirmRefresh="false" idColumn="detailid" orderBy="pr.goods_number:asc" queryAction="queryProdrequisitiondetail_item_view" url="/erpscan/save/detailaction" tableName="prodrequisitiondetail_item_view">
   <column label="商品编号" name="codeid" type="String" xid="default34"></column>
   <column label="商品名称" name="itemname" type="String" xid="default35"></column>
   <column label="商品规格" name="sformat" type="String" xid="default36"></column>
   <column label="产品信息" name="pcodeid" type="String" xid="default341"></column>
   <column label="产品名称" name="pitemname" type="String" xid="default352"></column>
   <column label="产品规格" name="psformat" type="String" xid="default363"></column>
   <column label="工单" name="worksheetid" type="String" xid="fxid16"></column>
   <column label="关联工单号" name="worksheetbillno" type="String" xid="fxid18"></column>
   <column label="关联工单产品批号" name="worksheetbatchno" type="String" xid="fxid27"></column>
   <column label="销售订单id" name="salesorderid" type="String" xid="fxid28"></column>
   <column label="销售订单明细id" name="salesdetailid" type="String" xid="fxid29"></column>
   <column label="关联订单号" name="order_id" type="String" xid="default365"></column>
   <column label="原订单号" name="soriginalbill" type="String" xid="default367"></column>
   <column label="助记码" name="mcode" type="String" xid="default37"></column>
   <column label="商品分类" name="classid" type="String" xid="default38"></column>
   <column label="单位" name="unit" type="String" xid="default39"></column>
   <column label="图片" name="imgurl" type="String" xid="default40"></column>
   <column label="商品码" name="barcode" type="String" xid="default41"></column>
   <column label="属性1" name="property1" type="String" xid="default42"></column>
   <column label="属性2" name="property2" type="String" xid="default43"></column>
   <column label="属性3" name="property3" type="String" xid="default44"></column>
   <column label="属性4" name="property4" type="String" xid="default45"></column>
   <column label="属性5" name="property5" type="String" xid="default46"></column>
   <column label="商品分类" name="classname" type="String" xid="default47"></column>
   <column label="编号" name="detailid" type="String" xid="default48"></column>
   <column label="主表编号" name="prodrequisitionid" type="String" xid="default49"></column>
   <column label="序号" name="goods_number" type="Integer" xid="default50"></column>
   <column label="企业编号" name="companyid" type="String" xid="default51"></column>
   <column label="业务日期" name="operate_time" type="Date" xid="default52"></column>
   <column label="经手人" name="operate_by" type="String" xid="default53"></column>
   <column label="业务单号" name="orderid" type="String" xid="default54"></column>
   <column label="商品编号" name="itemid" type="String" xid="default55"></column>
   <column label="数量" name="count" type="Double" xid="default60"></column>
   <column label="单价" name="price" type="Double" xid="default61"></column>
   <column label="金额" name="total" type="Double" xid="default62"></column>
   <column label="状态" name="status" type="String" xid="default63"></column>
   <column label="备注" name="remark" type="String" xid="default64"></column>
   <column label="创建人ID" name="create_id" type="String" xid="default65"></column>
   <column label="创建人" name="create_by" type="String" xid="default66"></column>
   <column label="创建时间" name="create_time" type="DateTime" xid="default67"></column>
   <column label="更新人ID" name="update_id" type="String" xid="default68"></column>
   <column label="更新人" name="update_by" type="String" xid="default69"></column>
   <column label="更新时间" name="update_time" type="DateTime" xid="default70"></column>
   <column label="原单号" name="originalbill" type="String" xid="default71"></column>
   <column label="批号" name="batchno" type="String" xid="default72"></column>
   <column label="仓库" name="houseid" type="String" xid="xid12"></column>
   <column label="领用部门" name="customerid" type="String" xid="xid13"></column>
   <column label="类型" name="stype" type="String" xid="xid15"></column>
   <column label="仓库编号" name="housecode" type="String" xid="xid21"></column>
   <column label="仓库名称" name="housename" type="String" xid="xid22"></column>
   <column name="unitstate1" type="Integer" xid="oounit1"></column>
   <column name="unitstate2" type="Integer" xid="oounit2"></column>
   <column name="unitstate3" type="Integer" xid="oounit3"></column>
   <column name="unitset1" type="String" xid="oounit4"></column>
   <column name="unitset2" type="String" xid="oounit5"></column>
   <column name="unitset3" type="String" xid="oounit6"></column>
   <column name="counttounit1" type="String" xid="oounit7"></column>
   <column name="counttounit2" type="String" xid="oounit8"></column>
   <column name="counttounit3" type="String" xid="oounit9"></column></div></div>  
  <div component="$UI/system/components/justep/controlGroup/controlGroup" class="x-control-group gridoverflow"
    title="生产领用单详情" xid="controlGroup1" style="position:relative;"> 
    <div class="x-control-group-title" xid="controlGroupTitle1"> 
      <span xid="span2">生产领用单详情</span>  
      <span xid="statusspan"/> 
    </div>  
    <a component="$UI/system/components/justep/button/button" class="btn btn-sm refeshbtn"
      label=" 打印" xid="printbtn" icon="icon-android-printer" style="right:0px;display:none;"
      onClick="printbtnClick"> 
      <i xid="i1" class="icon-android-printer"/>  
      <span xid="span1">打印</span> 
    </a>  
    <div xid="div8"> 
      <div component="$UI/system/components/justep/toolBar/toolBar" class="x-toolbar form-inline x-toolbar-spliter"
        xid="toolBar2"> 
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row4"> 
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col10"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit1"> 
              <label class="x-label" xid="label1" style="width:80px;"><![CDATA[单据编号：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output1" bind-ref="$model.prodrequisitionData.ref(&quot;orderid&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col11"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit4"> 
              <label class="x-label" xid="label4" style="width:80px;"><![CDATA[领用日期：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output5" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.prodrequisitionData.ref(&quot;operate_time&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col12"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit6"> 
              <label class="x-label" xid="label6" style="width:80px;"><![CDATA[原单号：]]></label>  
              <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm linkbtn" xid="billbtn" icon="icon-edit" onClick="billbtnClick" style="display:none;">
   <i xid="i3" class="icon-edit"></i>
   <span xid="span4"></span></a><div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output6" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.prodrequisitionData.ref(&quot;originalbill&quot;)"/> 
            </div> 
          </div> 
        </div>  
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row5"> 
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col14"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit2"> 
              <label class="x-label" xid="label2" style="width:80px;"><![CDATA[工单编号：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output2" bind-ref="$model.prodrequisitionData.ref(&quot;billno&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col2">
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit3"> 
              <label class="x-label" xid="label3" style="width:80px;"><![CDATA[领用部门：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output3" bind-ref="$model.prodrequisitionData.ref(&quot;customername&quot;)"/> 
            </div>
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col1"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit5"> 
              <label class="x-label" xid="label5" style="width:80px;">经手人：</label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output4" bind-ref="$model.prodrequisitionData.ref(&quot;staffname&quot;)"/> 
            </div>
          </div>
        </div>  
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row1"> 
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col3">
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit11"> 
              <label class="x-label" xid="label11" style="width:80px;"><![CDATA[商品编号：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output11" bind-ref="$model.prodrequisitionData.ref(&quot;codeid&quot;)"/>
            </div>
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col15"
            style="text_align:left;work-break:break-all;"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit10"> 
              <label class="x-label" xid="label10" style="width:80px;"><![CDATA[商品名称：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output10" bind-ref="$model.prodrequisitionData.ref(&quot;itemname&quot;)"
                style="text_align:left;work-break:break-all;"/>
            </div>
          </div>
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col13"
            style="text_align:left;work-break:break-all;"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit12"> 
              <label class="x-label" xid="label12" style="width:80px;">商品规格：</label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output12" bind-ref="$model.prodrequisitionData.ref(&quot;sformat&quot;)"/>
            </div>
          </div> 
        </div>
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row2"> 
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col17"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit9"> 
              <label class="x-label" xid="label7" style="width:80px;"><![CDATA[制单人：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output8" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.prodrequisitionData.ref(&quot;create_by&quot;)"/> 
            </div>
          </div>
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col16"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit7"> 
              <label class="x-label" xid="label8" style="width:80px;"><![CDATA[制单时间：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output9" dataType="Date" format="yyyy-MM-dd HH:mm" bind-ref="$model.prodrequisitionData.ref(&quot;create_time&quot;)"/> 
            </div>
          </div>
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col18"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit8"> 
              <label class="x-label" xid="label9" style="width:80px;"><![CDATA[备注：]]></label>  
              <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm linkbtn" xid="mainremarkbtn" icon="icon-edit" onClick="mainremarkbtnClick" style="display:none;">
   <i xid="i5" class="icon-edit"></i>
   <span xid="span6"></span></a><div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output7" bind-ref="$model.prodrequisitionData.ref(&quot;remark&quot;)"/> 
            </div>
          </div>
        </div>
      <div component="$UI/system/components/bootstrap/row/row" class="row" xid="row3">
   <div class="col col-xs-4" xid="col4"><div component="$UI/system/components/justep/labelEdit/labelEdit" class="x-label-edit x-label30" xid="labelEdit13">
   <label class="x-label" xid="label13" style="width:125px;"><![CDATA[关联排产领料单：]]></label>
   <div component="$UI/system/components/justep/output/output" class="x-output x-edit" xid="output13" bind-ref='$model.prodrequisitionData.ref("schedule_pick_billno")'></div></div></div>
   <div class="col col-xs-4" xid="col5"></div>
   <div class="col col-xs-4" xid="col6"></div></div></div>  
      <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
        altRows="false" class="x-grid-title-center gridoverflow" xid="grid2" height="auto"
        data="prodrequisitiondetailData" width="100%" useFooter="true" onCellRender="grid2CellRender"
        serverSort="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout"
        onReload="grid2Reload"> 
        <columns xid="columns4"> 
          <column width="40" name="goods_number" xid="column9" align="center" sorttype="int" />  
            <column width="80" name="worksheetbillno" xid="vcolumn1" align="center"/>  
          <column width="140" name="order_id" xid="vcolumn2" align="center"/>  
          <column width="150" name="pcodeid" xid="vcolumn3" sortable="false" align="center"/>  
          <column width="40" name="imgurl" xid="column3" align="center"/>  
          <column width="80" name="codeid" xid="column3" sortable="false" align="center"/>  
          <column width="120" name="barcode" xid="column11" sortable="false" align="center"/>  
          <column width="100" name="itemname" xid="column2" sortable="false" footerData="&quot;合计&quot;"
            align="center"/>  
          <column width="120" name="sformat" xid="column10" align="center"/>  
          <column width="80" name="property1" xid="column30" hidden="true" align="center"/>  
          <column width="80" name="property2" xid="column29" hidden="true" align="center"/>  
          <column width="80" name="property3" xid="column28" hidden="true" align="center"/>  
          <column width="80" name="property4" xid="column11" hidden="true" align="center"/>  
          <column width="80" name="property5" xid="column10" hidden="true" align="center"/>  
          <column width="100" name="batchno" xid="column4" align="center" /><column width="40" name="unit" xid="column8" sortable="false" align="center"/>  
          <column width="80" name="housename" xid="column1" align="center"/>
          <column width="100" name="count"  sorttype="float" xid="column6" footerData="$model.cutZero(($data.sum(&quot;count&quot;)).toFixed($model.countbit))"
            align="center"/>  
          <column width="80" name="counttounit1" xid="ucol1" align="center" sortable="false" hidden="true"></column>
          <column width="80" name="counttounit2" xid="ucol2" align="center" sortable="false" hidden="true"></column>
          <column width="80" name="counttounit3" xid="ucol3" align="center" sortable="false" hidden="true"></column>
          <column width="100" name="price" sorttype="float" xid="column5" align="center"/>
          <column width="100" name="total"  sorttype="float" xid="column7" align="center" footerData="$model.cutZero(($data.sum(&quot;total&quot;)).toFixed($model.moneybit))"/>
          <column width="150" name="remark" xid="column8" align="center"/>  
            
          <column width="80" name="classname" xid="column31" align="center"/> 
        </columns> 
      </div> 
    <div component="$UI/system/components/justep/pagerBar/pagerBar" class="x-pagerbar container-fluid" xid="pagerBar1" data="prodrequisitiondetailData">
   <div class="row" xid="div1">
    <div class="col-sm-3" xid="div2">
     <div class="x-pagerbar-length" xid="div3">
      <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect" class="x-pagerlimitselect" xid="pagerLimitSelect1">
       <span xid="span3">显示</span>
       <select component="$UI/system/components/justep/select/select" class="form-control input-sm" xid="select1">
        <option value="10" xid="default2">10</option>
        <option value="20" xid="default3">20</option>
        <option value="50" xid="default4">50</option>
        <option value="100" xid="default5">100</option></select> 
       <span xid="span5">条</span></label> </div> </div> 
    <div class="col-sm-3" xid="div4">
     <div class="x-pagerbar-info" xid="div5">当前显示0条，共0条</div></div> 
    <div class="col-sm-6" xid="div6">
     <div class="x-pagerbar-pagination" xid="div7">
      <ul class="pagination" component="$UI/system/components/bootstrap/pagination/pagination" xid="pagination1">
       <li class="prev" xid="li1">
        <a href="#" xid="a1">
         <span aria-hidden="true" xid="span7">«</span>
         <span class="sr-only" xid="span8">Previous</span></a> </li> 
       <li class="next" xid="li2">
        <a href="#" xid="a2">
         <span aria-hidden="true" xid="span9">»</span>
         <span class="sr-only" xid="span10">Next</span></a> </li> </ul> </div> </div> </div> </div></div> 
  </div>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="pringDialog"
    status="normal" width="306mm" height="95%"/> 
<a component="$UI/system/components/justep/button/button" class="btn" xid="modifybillrefresh" id="prodrequisition_modifybillrefresh" style="display:none;" onClick="modifybillrefreshClick">
   <i xid="kfi17"></i>
   <span xid="kfspan20"></span></a></div>

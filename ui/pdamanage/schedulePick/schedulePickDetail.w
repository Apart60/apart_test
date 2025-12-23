<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:m;">  
  <div component="$UI/system/components/justep/model/model" xid="model" onParamsReceive="modelParamsReceive"
    onLoad="modelLoad" style="width:164px;top:9px;left:115px;height:auto;"> 
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="schedulePickData" limit="1" confirmDelete="false" confirmRefresh="false"
      orderBy="create_time:desc" idColumn="schedule_pick_id" onBeforeRefresh="schedulePickDataBeforeRefresh"> 
      <column label="编号" name="schedule_pick_id" type="String" xid="default1"/>  
      <column label="单据类型" name="bill_type" type="String" xid="default2"/>  
      <column label="企业编号" name="companyid" type="String" xid="default3"/>  
      <column label="单据编号" name="orderid" type="String" xid="default4"/>  
      <column label="单据日期" name="operate_time" type="Date" xid="default5"/>  
      <column label="经手人" name="operate_by" type="String" xid="default6"/>  
      <column label="仓库" name="houseid" type="String" xid="default7"/>  
      <column label="单位部门" name="customerid" type="String" xid="default8"/>  
      <column label="数量" name="count" type="Double" xid="default9"/>  
      <column label="总额" name="total" type="Double" xid="default10"/>  
      <column label="状态" name="status" type="String" xid="default11"/>  
      <column label="备注" name="remark" type="String" xid="default12"/>  
      <column label="打印次数" name="printing" type="Integer" xid="default13"/>  
      <column label="导出次数" name="outexcel" type="Integer" xid="default14"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default15"/>  
      <column label="创建人" name="create_by" type="String" xid="default16"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default17"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default18"/>  
      <column label="更新人" name="update_by" type="String" xid="default19"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default20"/>  
      <column label="原单号" name="originalbill" type="String" xid="default21"/>  
      <column label="属性列表" name="iproperty" type="String" xid="default22"/>  
      <column label="往来单位编号" name="customercode" type="String" xid="default23"/>  
      <column label="往来单位名称" name="customername" type="String" xid="default24"/>  
      <column label="仓库编号" name="housecode" type="String" xid="default25"/>  
      <column label="仓库名称" name="housename" type="String" xid="default26"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default27"/>  
      <column label="员工姓名" name="staffname" type="String" xid="default28"/>
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="schedulePickDetailData" limit="999999" confirmDelete="false" confirmRefresh="false"
      idColumn="detailid" orderBy="goods_number:asc" onBeforeRefresh="schedulePickDetailDataBeforeRefresh"> 
      <column label="商品编号" name="codeid" type="String" xid="default29"/>  
      <column label="商品名称" name="itemname" type="String" xid="default30"/>  
      <column label="商品规格" name="sformat" type="String" xid="default31"/>  
      <column label="助记码" name="mcode" type="String" xid="default32"/>  
      <column label="商品分类" name="classid" type="String" xid="default33"/>  
      <column label="单位" name="unit" type="String" xid="default34"/>  
      <column label="图片" name="imgurl" type="String" xid="default35"/>  
      <column label="商品码" name="barcode" type="String" xid="default36"/>  
      <column label="属性1" name="property1" type="String" xid="default37"/>  
      <column label="属性2" name="property2" type="String" xid="default38"/>  
      <column label="属性3" name="property3" type="String" xid="default39"/>  
      <column label="属性4" name="property4" type="String" xid="default40"/>  
      <column label="属性5" name="property5" type="String" xid="default41"/>  
      <column label="商品分类" name="classname" type="String" xid="default42"/>  
      <column label="编号" name="detailid" type="String" xid="default43"/>  
      <column label="主表编号" name="schedule_pick_id" type="String" xid="default44"/>  
      <column label="序号" name="goods_number" type="Integer" xid="default45"/>  
      <column label="企业编号" name="companyid" type="String" xid="default46"/>  
      <column label="单据日期" name="operate_time" type="Date" xid="default47"/>  
      <column label="经手人" name="operate_by" type="String" xid="default48"/>  
      <column label="单据编号" name="orderid" type="String" xid="default49"/>  
      <column label="商品编号" name="itemid" type="String" xid="default50"/>  
      <column label="仓库" name="houseid" type="String" xid="default51"/>  
      <column label="单位部门" name="customerid" type="String" xid="default52"/>  
      <column label="类型" name="stype" type="String" xid="default53"/>  
      <column label="数量" name="count" type="Double" xid="default54"/>  
      <column label="单价" name="price" type="Double" xid="default55"/>  
      <column label="金额" name="total" type="Double" xid="default56"/>  
      <column label="状态" name="status" type="String" xid="default57"/>  
      <column label="备注" name="remark" type="String" xid="default58"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default59"/>  
      <column label="创建人" name="create_by" type="String" xid="default60"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default61"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default62"/>  
      <column label="更新人" name="update_by" type="String" xid="default63"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default64"/>  
      <column label="原单号" name="originalbill" type="String" xid="default65"/>  
      <column label="批号" name="batchno" type="String" xid="default66"/>  
      <column name="unitstate1" type="Integer" xid="oounit1"/>  
      <column name="unitstate2" type="Integer" xid="oounit2"/>  
      <column name="unitstate3" type="Integer" xid="oounit3"/>  
      <column name="unitset1" type="String" xid="oounit4"/>  
      <column name="unitset2" type="String" xid="oounit5"/>  
      <column name="unitset3" type="String" xid="oounit6"/>  
      <column name="counttounit1" type="String" xid="oounit7"/>  
      <column name="counttounit2" type="String" xid="oounit8"/>  
      <column name="counttounit3" type="String" xid="oounit9"/> 
    </div>
  </div>  
  <div component="$UI/system/components/justep/controlGroup/controlGroup" class="x-control-group gridoverflow"
    title="排产领料单详情" xid="controlGroup1" style="position:relative;"> 
    <div class="x-control-group-title" xid="controlGroupTitle1"> 
      <span xid="span2">排产领料单详情</span>  
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
                xid="output1" bind-ref="$model.schedulePickData.ref(&quot;orderid&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col11"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit4"> 
              <label class="x-label" xid="label4" style="width:80px;"><![CDATA[出库日期：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output5" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.schedulePickData.ref(&quot;operate_time&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col12"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit6"> 
              <label class="x-label" xid="label6" style="width:80px;"><![CDATA[原单号：]]></label>  
              <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm linkbtn" xid="billbtn" icon="icon-edit" onClick="billbtnClick" style="display:none;">
   <i xid="i2" class="icon-edit"></i>
   <span xid="span3"></span></a><div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output6" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.schedulePickData.ref(&quot;originalbill&quot;)"/> 
            </div> 
          </div> 
        </div>  
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row5"> 
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col15"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit3"> 
              <label class="x-label" xid="label3" style="width:80px;"><![CDATA[单位部门：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output3" bind-ref="$model.schedulePickData.ref(&quot;customername&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col14"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit2"> 
              <label class="x-label" xid="label2" style="width:80px;"><![CDATA[出库仓库：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output2" bind-ref="$model.schedulePickData.ref(&quot;housename&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col13"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit5"> 
              <label class="x-label" xid="label5" style="width:80px;">经手人：</label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output4" bind-ref="$model.schedulePickData.ref(&quot;staffname&quot;)"/> 
            </div> 
          </div> 
        </div>  
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row6"> 
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col18"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit8"> 
              <label class="x-label" xid="label9" style="width:80px;"><![CDATA[备注：]]></label>  
              <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm linkbtn"
                xid="mainremarkbtn" icon="icon-edit" onClick="mainremarkbtnClick" style="display:none;"> 
                <i xid="i5" class="icon-edit"/>  
                <span xid="span6"/>
              </a>
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output7" bind-ref="$model.schedulePickData.ref(&quot;remark&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col17"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit9"> 
              <label class="x-label" xid="label7" style="width:80px;"><![CDATA[制单人：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output8" dataType="Date" format="yyyy-MM-dd" bind-ref="$model.schedulePickData.ref(&quot;create_by&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col16"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit7"> 
              <label class="x-label" xid="label8" style="width:80px;"><![CDATA[制单时间：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="output9" dataType="Date" format="yyyy-MM-dd HH:mm" bind-ref="$model.schedulePickData.ref(&quot;create_time&quot;)"/> 
            </div> 
          </div> 
        </div> 
      </div>  
      <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
        altRows="false" class="x-grid-title-center gridoverflow" xid="grid2" height="auto"
        data="schedulePickDetailData" width="100%" useFooter="true" onCellRender="grid2CellRender"
        serverSort="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout"> 
        <columns xid="columns4"> 
          <column width="40" name="goods_number" xid="column9" align="center"/>  
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
          <column width="100" name="batchno" xid="column4" align="center" /><column width="40" name="unit" xid="column8" align="center"/>  
          <column width="100" name="count" xid="column6" footerData="$model.cutZero(($data.sum(&quot;count&quot;)).toFixed($model.countbit))"
            align="center"/>  
          <column width="80" name="counttounit1" xid="ucol1" align="center" sortable="false"
            hidden="true"/>  
          <column width="80" name="counttounit2" xid="ucol2" align="center" sortable="false"
            hidden="true"/>  
          <column width="80" name="counttounit3" xid="ucol3" align="center" sortable="false"
            hidden="true"/>  
          <column width="100" name="price" xid="column16" align="center"/>  
          <column width="100" name="total" xid="column14" align="center" footerData="$model.cutZero(($data.sum(&quot;total&quot;)).toFixed($model.moneybit))"/>  
          <column width="100" name="remark" xid="column8" align="center"/>  
            
          <column width="80" name="classname" xid="column31" align="center"/> 
        </columns> 
      </div> 
    </div> 
  </div>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="pringDialog"
    status="normal" width="306mm" height="95%"/>  
  <a component="$UI/system/components/justep/button/button" class="btn" xid="modifybillrefresh"
    id="schedule_pick_modifybillrefresh" style="display:none;" onClick="modifybillrefreshClick"> 
    <i xid="kfi17"/>  
    <span xid="kfspan20"/> 
  </a> 
</div>

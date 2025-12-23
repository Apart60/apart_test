<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:m;">  
  <div component="$UI/system/components/justep/model/model" xid="model" onParamsReceive="modelParamsReceive"
    onLoad="modelLoad"> 
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="prodrequisitionData" limit="1" confirmDelete="false" confirmRefresh="false"
      orderBy="create_time:desc" idColumn="prodrequisitionid" queryAction="queryProdrequisition_view"
      url="/erpscan/erpaction" tableName="prodrequisition"> 
      <column label="编号" name="prodrequisitionid" type="String" xid="default1"/>  
      <column label="单据类型" name="bill_type" type="String" xid="default7"/>  
      <column label="企业编号" name="companyid" type="String" xid="default8"/>  
      <column label="单据编号" name="orderid" type="String" xid="default9"/>  
      <column label="领用日期" name="operate_time" type="Date" xid="default10"/>  
      <column label="经手人" name="operate_by" type="String" xid="default5"/>  
      <column label="仓库" name="houseid" type="String" xid="default4"/>  
      <column label="领用部门" name="customerid" type="String" xid="default3"/>  
      <column label="数量" name="count" type="Double" xid="default2"/>  
      <column label="总额" name="total" type="Double" xid="default1"/>  
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
      <column label="部门编号" name="customerid" type="String" xid="default30"/>  
      <column label="领用部门" name="customername" type="String" xid="default31"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default32"/>  
      <column label="经手人" name="staffname" type="String" xid="default33"/>  
      <column label="工单编号" name="billno" type="String" xid="default99"/>  
      <column label="领用数量" name="worksheetcount" type="String" xid="default98"/>  
      <column label="商品编号" name="codeid" type="String" xid="default96"/>  
      <column label="商品名称" name="itemname" type="String" xid="default97"/>  
      <column label="商品规格" name="sformat" type="String" xid="default100"/>  
      <column label="排产领料单id" name="schedule_pick_id" type="String" xid="spid1"/> 
      <column label="排产领料单号" name="schedule_pick_billno" type="String" xid="spid2"/> 
      <column isCalculate="true" name="companyname" type="String" xid="xid8"/>  
      <column isCalculate="true" name="companyaddress" type="String" xid="xid9"/>  
      <column isCalculate="true" name="compnayphone" type="String" xid="xid10"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="columndata" idColumn="colid"> 
      <column label="列名" name="colid" type="String" xid="column21"/>  
      <column label="列名称" name="colname" type="String" xid="column23"/>  
      <column label="自定义列名" name="colnewname" type="String" xid="column24"/>  
      <column label="类型" name="type" type="Integer" xid="column26"/>  
      <column label="是否隐藏" name="ishide" type="Integer" xid="column25"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="prodrequisitiondetailData" limit="999999" confirmDelete="false" confirmRefresh="false"
      idColumn="detailid" orderBy="pr.goods_number:asc" queryAction="queryProdrequisitiondetail_item_view"
      url="/erpscan/save/detailaction" tableName="prodrequisitiondetail_item_view"
      onAfterRefresh="prodrequisitiondetailDataAfterRefresh"> 
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
      <column label="销售订单明细id" name="salesdetailid" type="String" xid="fxid29"/>  
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
      <column isCalculate="true" label="二维码" name="qcrode" type="String" xid="fxidstore24"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="pdata" idColumn="type"> 
      <data xid="default6">[{"type":"1"}]</data>  
      <column name="type" type="String" xid="xid11"/>  
      <column name="title" type="String" xid="column20"/>  
      <column name="address" type="String" xid="hxid16"/>  
      <column label="项目字体大小" name="maintitlesize" type="Integer" xid="hxid5"/>  
      <column label="项目字体大小" name="titlesize" type="Integer" xid="hxid1"/>  
      <column label="表格字体大小" name="othersize" type="Integer" xid="hxid2"/>  
       <column label="显示物料的二维码" name="showqrcode" type="Integer" xid="fhxid2"/>  
      <column name="staffname" type="Integer" xid="staffnamexid1"/>  
      <column name="create_by" type="Integer" xid="create_byxid2"/>
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="pagedata" idColumn="type"> 
      <column name="type" type="String" xid="xid5"/>  
      <column name="typename" type="String" xid="xid7"/>  
      <data xid="default73">[{"type":"1","typename":"宽*高 24.1cm*14cm"},{"type":"2","typename":"宽*高 24.1cm*28cm"},{"type":"3","typename":"宽*高 21cm*29.7cm"},{"type":"4","typename":"宽*高 29.7cm*21cm"}]</data> 
    </div> 
  </div>  
  <span component="$UI/system/components/justep/printHtml/printHtml" xid="printHtml1"
    target="printdiv"/>  
  <div component="$UI/system/components/justep/panel/panel" class="x-panel x-full"
    xid="panel1"> 
    <div class="x-panel-top" xid="top1"> 
      <div component="$UI/system/components/justep/titleBar/titleBar" title="生产领用单打印"
        class="x-titlebar" xid="titleBar1" style="background-color:white;"> 
        <div class="x-titlebar-title flex1" xid="title1" style="color:#4F77AA;">生产领用单打印</div>  
        <div class="x-titlebar-right reverse" xid="right1"> 
          <a component="$UI/system/components/justep/button/button" label="关闭"
            class="btn btncss" onClick="backBtnClick" xid="backBtn"> 
            <i xid="i1"/>  
            <span xid="span2">关闭</span> 
          </a>  
          <a component="$UI/system/components/justep/button/button" class="btn  btncss"
            label="打印" xid="printBtn" onClick="printBtnClick"> 
            <i xid="i6"/>  
            <span xid="span17">打印</span> 
          </a>  
          <a component="$UI/system/components/justep/button/button" class="btn  btncss"
            label="设置" xid="setBtn" onClick="setBtnClick"> 
            <i xid="i2"/>  
            <span xid="span1">设置</span> 
          </a> 
        </div> 
      </div> 
    </div>  
    <div class="x-panel-content" xid="content1" style="padding:10px;"> 
      <div component="$UI/system/components/justep/pagerBar/pagerBar" class="x-pagerbar container-fluid"
        xid="pagerBar1" data="prodrequisitiondetailData" style="margin:2px;"> 
        <div class="row" xid="div7"> 
          <div class="col-sm-3" xid="div8"> 
            <div class="x-pagerbar-length" xid="div9"> 
              <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect"
                class="x-pagerlimitselect" xid="pagerLimitSelect1" defaultValue="6"> 
                <span xid="span6">显示</span>  
                <select component="$UI/system/components/justep/select/select"
                  class="form-control input-sm" xid="select1"> 
                  <option value="5" xid="default142">5</option>  
                  <option value="6" xid="default143">6</option>  
                  <option value="12" xid="default153">12</option>  
                  <option value="13" xid="default154">13</option>  
                  <option value="19" xid="default163">19</option>  
                  <option value="20" xid="default164">20</option>  
                  <option value="21" xid="default165">21</option>  
                  <option value="22" xid="default161">22</option>  
                  <option value="23" xid="default161">23</option>  
                  <option value="24" xid="default161">24</option>  
                  <option value="25" xid="default161">25</option>  
                  <option value="26" xid="default161">26</option>  
                  <option value="27" xid="default161">27</option>  
                  <option value="28" xid="default161">28</option>  
                  <option value="29" xid="default161">29</option>  
                  <option value="30" xid="default161">30</option>  
                  <option value="31" xid="default161">31</option>  
                  <option value="32" xid="default161">32</option> 
                  <option value="100" xid="kdefault100">100</option>
                   <option value="200" xid="kdefault200">200</option>
                </select>  
                <span xid="span7">条</span> 
              </label> 
            </div> 
          </div>  
          <div class="col-sm-3" xid="div10"> 
            <div class="x-pagerbar-info" xid="div11">当前显示0条，共0条</div> 
          </div>  
          <div class="col-sm-6" xid="div12"> 
            <div class="x-pagerbar-pagination" xid="div13"> 
              <ul class="pagination" component="$UI/system/components/bootstrap/pagination/pagination"
                xid="pagination1"> 
                <li class="prev" xid="li2"> 
                  <a href="#" xid="a1"> 
                    <span aria-hidden="true" xid="span8">«</span>  
                    <span class="sr-only" xid="span9">Previous</span> 
                  </a> 
                </li>  
                <li class="next" xid="li3"> 
                  <a href="#" xid="a2"> 
                    <span aria-hidden="true" xid="span10">»</span>  
                    <span class="sr-only" xid="span11">Next</span> 
                  </a> 
                </li> 
              </ul> 
            </div> 
          </div> 
        </div> 
      </div>  
      <div xid="div14" align="right">
   <span component="$UI/system/components/justep/button/checkbox" class="x-checkbox" xid="showmoneyprintCKB" checkedValue="1" uncheckedValue="0" value="0" label="保留打印样式，不显示金额、单价的值" style="color:blue;margin-top:10px;" onChange="showmoneyprintCKBChange"></span></div><div xid="printdiv" style="padding:0px;margin:0px;"> 
        <div style="page-break-after:always;margin:0px;padding:30px 30px 8px 30px;width:24.1cm;"
          xid="contendiv"> 
          <div xid="div20" style="vertical-align:middle;text-align:center;height:100%;width:100%;"
            align="center"> 
            <div xid="div17" align="center" style="position: relative;display:flex;flex-direction:row;justify-content: center;align-items:center;width:100%;height:60px;"> 
              <div xid="div16" class="imageshow" style="margin-right:5px;"> 
                <img src=" " alt="" xid="image2" height="55px" style="width:auto;"/> 
              </div>  
              <div xid="div18" style="width:auto;"> 
                <div component="$UI/system/components/justep/row/row" class="x-row"
                  xid="row8" style="padding:0px;margin:0px;height:33px;"> 
                  <div class="x-col x-col-fixed x-col-bottom titlecss" xid="col7"
                    style="width:100%;" bind-text=" $model.pdata.val(&quot;title&quot;)"/> 
                </div>  
                <div component="$UI/system/components/justep/row/row" class="x-row"
                  xid="row8" style="padding:0px;margin:0px;height:22px;" bind-visible=" "> 
                  <div class="x-col x-col-fixed x-col-top addresscss" xid="col7"
                    style="width:100%;font-size:13px;margin:0px;padding-top:0px;padding-right:10px;"
                    bind-text=" $model.pdata.val(&quot;address&quot;)"/> 
                </div> 
              </div>  
              <div xid="colpage" style="margin-left:10px;" class="totalBigcss"/>  
              <div xid="qrcodediv" align="center" style="display: block;justify-content:center;align-items:center;position: absolute;right:20px;height:75px;width:75px;top:-11px;"> 
                <div component="$UI/system/components/justep/qrcode/qrcode"
                  xid="qrcode2" bind-content=" $model.prodrequisitionData.val(&quot;orderid&quot;)"
                  style="height:1.8cm;width:1.8cm;"/> 
              </div> 
            </div>  
            <div component="$UI/system/components/justep/row/row" class="x-row"
              xid="row3" style="padding:0px;margin:0px;min-height:23px;" bind-visible="($model.getColunmname(&quot;originalbill&quot;)+$model.getColunmname(&quot;orderid&quot;))!=&quot;&quot;"> 
              <div class="x-col x-col-fixed x-col-center  fontcss" xid="col20"
                style="text-align:left;word-break: break-all;width:62%;" bind-html="$model.getColunmname(&quot;originalbill&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;originalbill&quot;)+$model.prodrequisitionData.val(&quot;originalbill&quot;)"/>  
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col26"
                style="width:38%;text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;orderid&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;orderid&quot;)+$model.prodrequisitionData.val(&quot;orderid&quot;)"/>
            </div>  
            <div component="$UI/system/components/justep/row/row" class="x-row"
              xid="row4" style="padding:0px;margin:0px;min-height:23px;" bind-visible="($model.getColunmname(&quot;operate_time&quot;)+$model.getColunmname(&quot;customername&quot;))!=&quot;&quot;"> 
              <div class="x-col x-col-fixed x-col-center  fontcss" xid="col4"
                style="width:62%;text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;customername&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;customername&quot;)+$model.prodrequisitionData.val(&quot;customername&quot;)"/>  
              <div class="x-col x-col-fixed x-col-center  fontcss" xid="col3"
                style="width:38%;text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;operate_time&quot;)==&quot;&quot;?&quot;&quot;:$model.prodrequisitionData.val(&quot;operate_time&quot;)==undefined?&quot;&quot;:$model.getColunmname(&quot;operate_time&quot;)+ justep.Date.toString($model.prodrequisitionData.val(&quot;operate_time&quot;), justep.Date.STANDART_FORMAT_SHOT)"/>
            </div>  
            <div component="$UI/system/components/justep/row/row" class="x-row"
              xid="row7" style="padding:0px;margin:0px;min-height:23px;" bind-visible="($model.getColunmname(&quot;mcodeid&quot;)+$model.getColunmname(&quot;mitemname&quot;)+$model.getColunmname(&quot;msformat&quot;))!=&quot;&quot;"> 
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col5"
                style="width:30%;" bind-html="$model.getColunmname(&quot;mcoedid&quot;)==&quot;&quot;?&quot;&quot;:($model.getColunmname(&quot;mcoedid&quot;)+($model.prodrequisitionData.val(&quot;codeid&quot;) ==undefined ? &quot;&quot; :$model.prodrequisitionData.val(&quot;codeid&quot;)))"/>  
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col39"
                style="width:32%; text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;mitemname&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;mitemname&quot;)+($model.prodrequisitionData.val(&quot;itemname&quot;) ==undefined ? &quot;&quot; :$model.prodrequisitionData.val(&quot;itemname&quot;))"/>  
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col37"
                style="width:38%;text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;msformat&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;msformat&quot;)+ ($model.prodrequisitionData.val(&quot;sformat&quot;)==undefined?&quot;&quot;:$model.prodrequisitionData.val(&quot;sformat&quot;))"/> 
            </div>  
            <div component="$UI/system/components/justep/row/row" class="x-row"
              xid="row6" style="padding:0px;margin:0px;min-height:23px;" bind-visible="($model.getColunmname(&quot;billno&quot;)+$model.getColunmname(&quot;remark&quot;))!=&quot;&quot; "> 
              <div class="x-col x-col-fixed x-col-center  fontcss" xid="col38"
                style="width:30%;text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;billno&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;billno&quot;)+($model.prodrequisitionData.val(&quot;billno&quot;)==undefined ? &quot;&quot; : $model.prodrequisitionData.val(&quot;billno&quot;))"/>
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col36"
                style="width:70%;text-align:left;word-break: break-all;" bind-html="$model.getColunmname(&quot;remark&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;remark&quot;)+$model.prodrequisitionData.val(&quot;remark&quot;)"/> 
            </div>  
            <div component="$UI/system/components/justep/row/row" class="x-row" xid="row1" bind-visible='$model.getColunmname("schedule_pick_billno")!=""'>
   <div class="x-col x-col-fixed x-col-center  fontcss" xid="col1" style="width:70%;text-align:left;word-break: break-all;" bind-html='$model.getColunmname("schedule_pick_billno")==""?"":$model.getColunmname("schedule_pick_billno")+$model.prodrequisitionData.val("schedule_pick_billno")'></div>
   </div><div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
              altRows="false" class="x-grid-title-center center-block" xid="grid1"
              data="prodrequisitiondetailData" headerMenu="hideColumn,setColumn,saveLayout"
              onCellRender="grid1CellRender" rowAttr="{style:&quot;height:36px;&quot;}" useFooter="true"
              moveColumn="true" height="auto" width="100%" onReload="grid1Reload"> 
              <columns xid="columns1"> 
                <column name="goods_number" xid="column2" align="center" sortable="false"
                  width="40"/>  
                <column name="housename" xid="column9" align="center" sortable="false"/>  
                <column name="worksheetbillno" xid="vcolumn1" align="center" sortable="false"/>  
                <column name="order_id" xid="vcolumn2" align="center" sortable="false"/>  
                <column name="pcodeid" xid="vcolumn3" sortable="false" align="center"/>  
                <column name="imgurl" xid="column1" align="center" hidden="true"
                  sortable="false"/>  
                <column name="codeid" xid="column3" align="center" sortable="false"/>  
                <column name="itemname" xid="column4" align="center" sortable="false"
                  footerData="&quot;合计&quot;"> 
                  <group xid="default11"/> 
                </column>  
                <column name="batchno" xid="column13" align="center" sortable="false"/>  
                <column name="sformat" xid="column5" align="center" sortable="false"/>  
                <column name="barcode" xid="column8" align="center" sortable="false"/>  
                <column name="property1" xid="column14" align="center" hidden="false"
                  sortable="false"/>  
                <column name="property2" xid="column15" align="center" hidden="false"
                  sortable="false"/>  
                <column name="property3" xid="column16" align="center" hidden="false"
                  sortable="false"/>  
                <column name="property4" xid="column17" align="center" hidden="false"
                  sortable="false"/>  
                <column name="property5" xid="column18" align="center" hidden="false"
                  sortable="false"/>  
                <column name="classname" xid="column7" align="center" sortable="false"/>  
                <column name="unit" xid="column6" align="center" sortable="false"/>  
                <column name="count" xid="column10" align="center" sortable="false"
                  footerData="$model.cutZero(($data.sum(&quot;count&quot;)).toFixed($model.countbit))"/>  
                <column name="counttounit1" xid="uu_column36" align="center" sortable="false"/>  
                <column name="counttounit2" xid="uu_column37" align="center" sortable="false"/>  
                <column name="counttounit3" xid="uu_column38" align="center" sortable="false"/>  
                <column name="price" xid="column11" align="center" sortable="false"/>  
                <column name="total" xid="column19" align="center" sortable="false"
                  footerData="$model.cutZero(($data.sum(&quot;total&quot;)).toFixed($model.moneybit))"/>  
                <column name="remark" xid="column12" align="center" sortable="false"/> 
                 <column name="qcrode" xid="fcolumn33" align="center" sortable="true"
                  hidden="true"/> 
              </columns> 
            </div>  
            <div component="$UI/system/components/justep/row/row" class="x-row"
              xid="signnamerow" style="padding:0px;margin-left:0px;margin-top:5px;min-height:25px;"
              bind-visible="$model.getColunmname(&quot;signname&quot;) +$model.getColunmname(&quot;mcount&quot;)+$model.getColunmname(&quot;staffname&quot;)+$model.getColunmname(&quot;create_by&quot;) !=&quot;&quot;"> 
              <div class="x-col x-col-fixed x-col-center  fontcss" xid="col2"
                style="width:25%;" bind-html="$model.getColunmname(&quot;mcount&quot;)==&quot;&quot;?&quot;&quot;:$model.getColunmname(&quot;mcount&quot;)+$model.prodrequisitionData.val(&quot;count&quot;)"/>
              <div class="x-col x-col-fixed x-col-center  fontcss" xid="col11"
                style="width:25%;" bind-text="$model.getColunmname(&quot;signname&quot;)"/>
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col14"
                style="width:25%;" bind-text='$model.getColunmname("staffname")==""?"":$model.getColunmname("staffname")+($model.pdata.val("staffname")==1?$model.prodrequisitionData.val("staffname"):"")'/>
              <div class="x-col x-col-fixed x-col-center   fontcss" xid="col15"
                style="width:25%;" bind-html='$model.getColunmname("create_by")==""?"":$model.getColunmname("create_by")+($model.prodrequisitionData.val("create_by")!=undefined &amp;&amp; $model.pdata.val("create_by")==1?$model.prodrequisitionData.val("create_by").split("[")[0]:"")'/>
            </div>
          </div> 
        </div> 
      </div> 
    </div> 
  </div>  
  <div component="$UI/system/components/justep/popOver/popOver" class="x-popOver"
    direction="auto" xid="setpopOver" position="right" dismissible="false"> 
    <div class="x-popOver-overlay" xid="div2"/>  
    <div class="x-popOver-content" xid="div3"> 
      <div xid="contentdiv" class="listpop" style="width:350px;"> 
        <div xid="div5" bind-text="&quot;生产领用单列别名设置&quot;" align="center" style="font-size:16px;font-weight:bold;margin:10px;"/>  
        <div xid="div6" align="right" style="margin-bottom:10px;"> 
          <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm btncss"
            label="恢复默认" xid="retsetBtn" onClick="retsetBtnClick"> 
            <i xid="i4"/>  
            <span xid="span4">恢复默认</span> 
          </a>  
          <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm btncss"
            label="保存设置" xid="savesetBtn" onClick="savesetBtnClick"> 
            <i xid="i3"/>  
            <span xid="span3">保存设置</span> 
          </a>  
          <a component="$UI/system/components/justep/button/button" class="btn btn-link btn-sm btncss"
            label="关闭" xid="colsebtn" onClick="colsebtnClick"> 
            <i xid="i5"/>  
            <span xid="span5">关闭</span> 
          </a> 
        </div>  
        <div xid="speddiv"> 
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label30" xid="flabelEdit1"> 
            <label class="x-label" xid="label1">纸张尺寸</label>  
            <span component="$UI/system/components/justep/select/radioGroup"
              class="x-radios x-edit" xid="pageradioGroup" bind-itemset="pagedata"
              bind-itemsetValue="ref(&quot;type&quot;)" bind-itemsetLabel="ref(&quot;typename&quot;)"
              onChange="pageradioGroupChange" bind-ref="$model.pdata.ref(&quot;type&quot;)"/> 
          </div>  
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label20" xid="flabelEdit2"> 
            <label class="x-label" xid="label2">标题</label>  
            <textarea component="$UI/system/components/justep/textarea/textarea"
              class="form-control x-edit" xid="ftextarea1" bind-ref="$model.pdata.ref(&quot;title&quot;)"/> 
          </div>  
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label20" xid="flabelEdit3"> 
            <label class="x-label" xid="label3">地址</label>  
            <textarea component="$UI/system/components/justep/textarea/textarea"
              class="form-control x-edit" xid="textarea2" bind-ref="$model.pdata.ref(&quot;address&quot;)"
              style="height:80px;"/> 
          </div>  
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label60" xid="flabelEdit18"> 
            <label class="x-label" xid="label4" style="width:100px;text-align:left;font-weight:bold;color:#4f77aa;">标题字体大小</label>  
            <input component="$UI/system/components/justep/input/input" class="form-control x-edit input-sm"
              xid="titleinput" dataType="Integer" bind-ref="$model.pdata.ref(&quot;maintitlesize&quot;)"
              onChange="titleinputChange"/>  
            <span xid="span12" style="margin-left:3px;text-align:center;font-weight:bold;color:#4f77aa;">px</span> 
          </div>  
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label60" xid="flabelEdit17"> 
            <label class="x-label" xid="label5" style="width:100px;text-align:left;font-weight:bold;color:#4f77aa;">项目字体大小</label>  
            <input component="$UI/system/components/justep/input/input" class="form-control x-edit input-sm"
              xid="finput4" dataType="Integer" onChange="finput4Change" bind-ref="$model.pdata.ref(&quot;titlesize&quot;)"/>  
            <span xid="span13" style="margin-left:3px;text-align:center;font-weight:bold;color:#4f77aa;">px</span> 
          </div>  
          <div component="$UI/system/components/justep/labelEdit/labelEdit"
            class="x-label-edit x-label60" xid="flabelEdit16"> 
            <label class="x-label" xid="label5" style="width:100px;text-align:left;font-weight:bold;color:#4f77aa;">表格字体大小</label>  
            <input component="$UI/system/components/justep/input/input" class="form-control x-edit input-sm"
              xid="finput3" dataType="Integer" onChange="finput3Change" bind-ref="$model.pdata.ref(&quot;othersize&quot;)"/>  
            <span xid="span12" style="margin-left:3px;text-align:center;font-weight:bold;color:#4f77aa;">px</span> 
          </div>  
          <div component="$UI/system/components/justep/labelEdit/labelEdit" class="x-label-edit x-label40" xid="labelEdit22">
   <label class="x-label" xid="label7" style="width:180px;text-align:left;font-weight:bold;color:#4f77aa;"><![CDATA[数据表显示商品的二维码列]]></label>
   <div xid="div27" class=" x-edit" align="left">
    <span component="$UI/system/components/justep/button/checkbox" class="x-checkbox x-edit" xid="checkbox3" onChange="checkbox3Change" checkedValue="1" uncheckedValue="0" bind-ref='$model.pdata.ref("showqrcode")'></span></div> </div><div xid="notshowdiv" style="color:blue;"> 
            <span component="$UI/system/components/justep/button/checkbox" class="x-checkbox"
              xid="staffnamecheckbox" label="显示经手人内容" checked="true" 
              checkedValue="1" uncheckedValue="0" value="1" bind-ref="$model.pdata.ref(&quot;staffname&quot;)"/>
          <span component="$UI/system/components/justep/button/checkbox" class="x-checkbox" xid="create_bycheckbox" label="显示制单人内容" checked="true"  checkedValue="1" uncheckedValue="0" value="1" bind-ref='$model.pdata.ref("create_by")'></span></div>
        </div>  
        <table component="$UI/system/components/justep/list/list" class="x-list"
          xid="list2" data="columndata"> 
          <thead xid="thead1"> 
            <tr xid="tr1"> 
              <th xid="default12" width="100">列名称</th>  
              <th xid="default13">自定义列名称</th> 
            </tr> 
          </thead>  
          <tbody class="x-list-template" xid="listTemplate1"> 
            <tr xid="tr2" bind-visible=" val(&quot;ishide&quot;) != 1"> 
              <td xid="td1"> 
                <div component="$UI/system/components/justep/output/output"
                  class="x-output" xid="output1" bind-ref="ref(&quot;colname&quot;)" style="font-size:12px;"/> 
              </td>  
              <td xid="td2"> 
                <input component="$UI/system/components/justep/input/input" class="form-control input-sm"
                  xid="input1" bind-ref="ref(&quot;colnewname&quot;)" onChange="input1Change"/> 
              </td> 
            </tr> 
          </tbody> 
        </table> 
      </div> 
    </div> 
  </div> 
</div>

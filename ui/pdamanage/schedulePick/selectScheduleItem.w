<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:m;">  
  <div component="$UI/system/components/justep/model/model" xid="model" style="top:209px;left:193px;height:auto;"
    onParamsReceive="modelParamsReceive" onLoad="modelLoad"> 
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="iteminfoData" limit="10" idColumn="no"> 
      <column isCalculate="true" label="序号" name="no" type="Integer" xid="xid11"/>  
      <rule xid="rule1"> 
        <col name="no" xid="ruleCol1"> 
          <calculate xid="calculate1"> 
            <expr xid="default16">$row.index()+1</expr> 
          </calculate> 
        </col> 
      </rule>  
      <column label="编号" name="itemid" type="String" xid="default7"/>  
      <column label="企业编号" name="companyid" type="String" xid="default8"/>  
      <column label="商品编号" name="codeid" type="String" xid="default9"/>  
      <column label="商品名称" name="itemname" type="String" xid="default10"/>  
      <column label="商品规格" name="sformat" type="String" xid="default11"/>  
      <column label="助记码" name="mcode" type="String" xid="default12"/>  
      <column label="商品分类" name="classid" type="String" xid="default13"/>  
      <column label="单位" name="unit" type="String" xid="default14"/>  
      <column label="图片" name="imgurl" type="String" xid="default15"/>  
      <column label="进货单价" name="inprice" type="Double" xid="default17"/>  
      <column label="零售单价" name="outprice" type="Double" xid="default18"/>  
      <column label="商品条码" name="barcode" type="String" xid="default19"/>  
      <column label="状态" name="status" type="String" xid="default21"/>  
      <column label="属性1" name="property1" type="String" xid="default22"/>  
      <column label="属性2" name="property2" type="String" xid="default23"/>  
      <column label="属性3" name="property3" type="String" xid="default24"/>  
      <column label="属性4" name="property4" type="String" xid="default25"/>  
      <column label="属性5" name="property5" type="String" xid="default26"/>  
      <column label="一级销售单价" name="outprice1" type="Double" xid="default33"/>  
      <column label="二级销售单价" name="outprice2" type="Double" xid="default34"/>  
      <column label="三级销售单价" name="outprice3" type="Double" xid="default38"/>  
      <column label="四级销售单价" name="outprice4" type="Double" xid="default39"/>  
      <column label="五级销售单价" name="outprice5" type="Double" xid="default40"/>  
      <column label="商品分类" name="classname" type="String" xid="default43"/>  
      <column label="仓库" name="houseid" type="String" xid="default44"/>  
      <column label="所在仓库" name="housename" type="String" xid="default45"/>  
      <column label="批号" name="batchno" type="String" xid="default46"/>  
      <column label="库存数量" name="count" type="Double" xid="default47"/>  
      <column label="库存金额" name="money" type="Double" xid="default48"/>  
      <column label="成本单价" name="newcostprice" type="Double" xid="default49"/>  
      <column label="备出库数" name="checkout_count" type="Double" xid="default50"/>  
      <column label="备注" name="remark" type="String" xid="xid5"/>  
      <column name="unitstate1" type="Integer" xid="unit1"/>  
      <column name="unitstate2" type="Integer" xid="unit2"/>  
      <column name="unitstate3" type="Integer" xid="unit3"/>  
      <column name="unitset1" type="String" xid="unit4"/>  
      <column name="unitset2" type="String" xid="unit5"/>  
      <column name="unitset3" type="String" xid="unit6"/>  
      <column name="counttounit1" type="String" xid="unit7"/>  
      <column name="counttounit2" type="String" xid="unit8"/>  
      <column name="counttounit3" type="String" xid="unit9"/>  
      <column label="BOM(生产)" name="splits" type="Integer" xid="bxid19"/>  
      <column label="BOM(物料)" name="msplits" type="Integer" xid="bxid20"/>  
      </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="classData" idColumn="classid" isTree="true"> 
      <treeOption xid="default37" parentRelation="parentid" delayLoad="false"/>  
      <column label="分类编号" name="classid" type="String" xid="xid14"/>  
      <column label="企业编号" name="companyid" type="String" xid="xid17"/>  
      <column label="分类名称" name="classname" type="String" xid="xid15"/>  
      <column label="父ID" name="parentid" type="String" xid="xid16"/> 
    </div>  
    <div component="$UI/system/components/justep/data/baasData" autoLoad="false"
      xid="itemclassData" queryAction="queryItemclass" saveAction="saveItemclass"
      url="/erpscan/pdaaction" tableName="itemclass" idColumn="classid" limit="-1"
      confirmDelete="false" confirmRefresh="false" orderBy="convert(classname using gbk):asc"
      onAfterRefresh="itemclassDataAfterRefresh"> 
      <column label="32位uuid" name="classid" type="String" xid="default122"/>  
      <column label="企业编号" name="companyid" type="String" xid="default121"/>  
      <column label="分类名" name="classname" type="String" xid="default120"/>  
      <filter name="filter0" xid="filter6"><![CDATA[companyid=:companyid]]></filter>  
      <column label="父级" name="parentid" type="String" xid="xid21"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="pagedata" idColumn="id"> 
      <column name="id" type="String" xid="column18"/>  
      <column name="offset" type="Integer" xid="column10"/>  
      <column name="limit" type="Integer" xid="column9"/>  
      <column name="total" type="Integer" xid="column8"/>  
      <column name="pagecount" type="Integer" xid="column8"/>  
      <column name="currentpage" type="Integer" xid="column10"/>  
      <column name="currentcount" type="Integer" xid="column7"/>  
      <column name="start" type="Integer" xid="xid4"/>  
      <column name="end" type="Integer" xid="column19"/>  
      <data xid="default100">[{"id":"AA","offset":0,"limit":10,"total":0,"pagecount":0,"currentpage":1,"currentcount":0,"start":0,"end":0}]</data> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="pagenumdata" idColumn="num"> 
      <column name="num" type="Integer" xid="column4"/> 
    </div> 
  </div>  
  <div class="x-panel-content gridoverflow" xid="content1" style="margin:0px;padding-left:15px;padding-right:10px;padding-bottom:50px;height:auto;"
    bind-keypress="findKeypress"> 
    <div component="$UI/system/components/bootstrap/row/row" class="row" xid="row2"> 
      <div class="col col-xs-12 col-sm-4 col-md-4 col-lg-4" xid="col3"> 
        <div component="$UI/system/components/justep/titleBar/titleBar" class="x-titlebar"
          xid="titleBar1" style="background-color:white;"> 
          <div class="x-titlebar-title" xid="title1" style="text-align:left;color:#000;"><![CDATA[选择商品]]></div> 
        </div> 
      </div>  
      <div class="col col-xs-12 col-sm-8 col-md-8 col-lg-8 flexdiv" xid="col4"> 
        <a component="$UI/system/components/justep/button/button" class="btn btn-sm x-dialog-button savebtn"
          label=" 选择并关闭" xid="okcloseBtn" onClick="okcloseBtnClick" icon="icon-android-checkmark"> 
          <i xid="i1" class="icon-android-checkmark"/>  
          <span xid="span6">选择并关闭</span> 
        </a>  
        <a component="$UI/system/components/justep/button/button" class="btn btn-sm x-dialog-button cancelbtn"
          label=" 关闭" xid="cancelbtn" onClick="cancelbtnClick" icon="icon-android-close"> 
          <i xid="i2" class="icon-android-close"/>  
          <span xid="span2">关闭</span> 
        </a> 
      </div> 
    </div>  
    <div component="$UI/system/components/justep/toolBar/toolBar" class="x-toolbar form-inline x-toolbar-spliter"
      xid="toolBar1"> 
      <input component="$UI/system/components/justep/input/input" class="form-control  input-sm toolbarbtn"
        xid="nameinput" placeHolder="商品编号/商品名称" dataType="String"/>  
      <input component="$UI/system/components/justep/input/input" class="form-control  input-sm toolbarbtn"
        xid="sformatinput" placeHolder="商品规格/批号" dataType="String"/>  
      <input type="text" class="form-control input-sm toolbarbtn" component="$UI/system/components/justep/input/input"
        xid="searchcontent" placeHolder="商品码/助记码/属性/备注"/>  
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left custombtn"
        xid="findBtn" label=" 查找" icon="linear linear-magnifier" onClick="findBtnClick"> 
        <i xid="i7" class="linear linear-magnifier"/>  
        <span xid="span14">查找</span> 
      </a>  
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left refeshbtn"
        onClick="refreshBtnClick" xid="refreshBtn" label=" 刷新" icon="dataControl dataControl-refresh"> 
        <i xid="i12" class="dataControl dataControl-refresh"/>  
        <span xid="span18">刷新</span> 
      </a> 
    </div>  
    <div component="$UI/system/components/bootstrap/row/row" class="row" xid="row1"> 
      <div class="col col-xs-12 col-sm-3 col-md-3 col-lg-2 col-xl-1" xid="col1"> 
        <div xid="div8" style="width:100%;border:2px solid #D7D7D7;padding-left:5px;padding-top:5px;"> 
          <span component="$UI/system/components/justep/button/checkbox" class="x-checkbox"
            xid="classcheckbox" label="包含下级" checked="true" onChange="classcheckboxChange"
            checkedValue="1" uncheckedValue="0" value="1"/>  
          <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
            altRows="false" class="x-grid-no-bordered gridoverflow" xid="grid2" data="classData"
            multiboxonly="false" multiselect="false" directEdit="true" height="auto"
            width="100%" expandColumn="classname" appearance="treeGrid" onRowClick="grid2RowClick"
            serverSort="false" onCellRender="grid2CellRender" headerMenu="setColumn,saveLayout"> 
            <columns xid="columns2"> 
              <column name="classname" xid="column6" sortable="false"/> 
            </columns> 
          </div> 
        </div> 
      </div>  
      <div class="col col-xs-12 col-sm-9 col-md-9 col-lg-10 col-xl-11" xid="col2"> 
        <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
          altRows="true" class="x-grid-no-bordered x-grid-title-center gridoverflow"
          xid="selectitemallgrid" width="100%" height="auto" data="iteminfoData" headerMenu="hideColumn,setColumn,saveLayout"
          directEdit="false" onCellRender="grid1CellRender" multiselect="true" onRowChecked="grid1RowChecked"
          moveColumn="true" showRowNumber="true" onRowCheckedAll="grid1RowChecked"
          multiboxonly="false"> 
          <columns xid="columns1"> 
            <column width="40" name="imgurl" xid="column17" sortable="false" align="center"/>  
            <column width="80" name="classname" xid="column31" align="center"/>  
            <column width="70" name="codeid" xid="column5" align="center"/>  
            <column width="100" name="itemname" xid="column12" align="center"/>  
            <column width="100" name="sformat" xid="column13" align="center"/>  
            <column width="80" name="housename" xid="column30" align="center"
              hidden="true"/>  
            <column width="40" name="unit" xid="column16" align="center"/>  
            <column width="80" name="count" xid="column1" align="center" sorttype="float"/>  
            <column width="80" name="counttounit1" xid="ucol1" align="center"
              sortable="false" hidden="true"/>  
            <column width="80" name="counttounit2" xid="ucol2" align="center"
              sortable="false" hidden="true"/>  
            <column width="80" name="counttounit3" xid="ucol3" align="center"
              sortable="false" hidden="true"/>  
            <column width="90" name="checkout_count" xid="column24" align="center"
              sorttype="float"/>  
            <column width="80" name="property1" xid="column251" hidden="true"
              align="center"/>  
            <column width="80" name="property2" xid="column261" hidden="true"
              align="center"/>  
            <column width="80" name="property3" xid="column271" hidden="true"
              align="center"/>  
            <column width="80" name="property4" xid="column281" hidden="true"
              align="center"/>  
            <column width="80" name="property5" xid="column291" hidden="true"
              align="center"/>  
            <column width="70" name="mcode" xid="column14" align="center"/>  
            <column width="120" name="barcode" xid="column22" align="center"/>  
            <column width="100" name="remark" xid="column23" align="center"/> 
          </columns> 
        </div>  
        <div xid="pagerbar"> 
          <div component="$UI/system/components/justep/row/row" class="x-row"
            xid="row3"> 
            <div class="x-col x-col-center" xid="col7"> 
              <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect"
                class="x-pagerlimitselect" xid="pagerLimitSelect2" defaultValue="10"> 
                <span xid="span1">显示</span>  
                <select component="$UI/system/components/justep/select/select"
                  class="form-control input-sm" xid="select2" bind-ref="$model.pagedata.ref(&quot;limit&quot;)"
                  onChange="limitChange"> 
                  <option value="10" xid="default99">10</option>  
                  <option value="20" xid="default97">20</option>  
                  <option value="50" xid="default98">50</option>  
                  <option value="100" xid="default96">100</option>
                </select>  
                <span xid="span4">条</span>
              </label> 
            </div>  
            <div class="x-col x-col-center" xid="col5"> 
              <div xid="div10" bind-text="'当前显示' + $model.pagedata.val(&quot;start&quot;) +' - ' +$model.pagedata.val(&quot;end&quot;)+'条，共' + $model.pagedata.val(&quot;total&quot;) +'条'"/>
            </div>  
            <div class="x-col x-col-fixed x-col-center" xid="col6" style="vertical-align:middle;width:auto;"> 
              <div component="$UI/system/components/justep/row/row" class="x-row minPM"
                xid="row3" style="text-align:right;"> 
                <div class="x-col x-col-fixed minPM" xid="col7" style="width:auto;"> 
                  <a component="$UI/system/components/justep/button/button"
                    class="btn preBtn" xid="firstpage" onClick="firstpageClick" bind-disable=" $model.pagedata.val(&quot;currentpage&quot;) ==1"
                    label="&lt;&lt;" style="margin-right:-5px;margin-left:0px;"> 
                    <i xid="i4"/>  
                    <span xid="span4">&lt;&lt;</span>
                  </a>  
                  <a component="$UI/system/components/justep/button/button"
                    class="btn noCheck" label="&lt;" xid="pre" onClick="preClick" bind-disable=" $model.pagedata.val(&quot;currentpage&quot;) ==1"> 
                    <i xid="i5"/>  
                    <span xid="span25">&amp;lt;</span>
                  </a> 
                </div>  
                <div class="x-col x-col-fixed minPM" xid="col8" style="width:auto;"> 
                  <div component="$UI/system/components/justep/list/list" class="x-list"
                    xid="list1" style="padding:0px;margin:0px;pagination" limit="7"
                    data="pagenumdata" disablePullToRefresh="true" disableInfiniteLoad="true"> 
                    <ul class="x-list-template row" xid="listTemplateUl1"
                      componentname="$UI/system/components/justep/list/list#listTemplateUl"
                      id="undefined_listTemplateUl1" style="padding:0px;margin:0px;"> 
                      <a component="$UI/system/components/justep/button/button"
                        class="btn " xid="pageBtn" bind-css="{'Check':$model.pagedata.val(&quot;currentpage&quot;) == val(&quot;num&quot;),'noCheck':$model.pagedata.val(&quot;currentpage&quot;) != val(&quot;num&quot;)}"
                        onClick="pageBtnClick" bind-text=" val(&quot;num&quot;)" bind-style="{'margin-right':  ($index() == $model.pagenumdata.count()-1)?'0px':'-4px'}"
                        bind-disable=" val(&quot;num&quot;) == $model.pagedata.val(&quot;currentpage&quot;)"> 
                        <i xid="i5"/>  
                        <span xid="span27"/>
                      </a> 
                    </ul> 
                  </div> 
                </div>  
                <div class="x-col x-col-fixed minPM" xid="col9" style="width:auto;"> 
                  <a component="$UI/system/components/justep/button/button"
                    class="btn noCheck" label="&gt;" xid="next" onClick="nextClick" bind-disable=" $model.pagedata.val(&quot;pagecount&quot;) &lt;= $model.pagedata.val(&quot;currentpage&quot;)"> 
                    <i xid="i6"/>  
                    <span xid="span26">&gt;</span>
                  </a>  
                  <a component="$UI/system/components/justep/button/button"
                    class="btn nextBtn" xid="lastpage" onClick="lastpageClick" bind-disable=" $model.pagedata.val(&quot;pagecount&quot;) &lt;= $model.pagedata.val(&quot;currentpage&quot;)"
                    label="&gt;&gt;" style="margin-right:0px;margin-left:-5px;"> 
                    <i xid="i3"/>  
                    <span xid="span1">&amp;gt;&amp;gt;</span>
                  </a> 
                </div> 
              </div> 
            </div> 
          </div> 
        </div>
      </div> 
    </div> 
  </div> 
</div>

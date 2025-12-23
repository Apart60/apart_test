<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:pc">  
  <div component="$UI/system/components/justep/model/model" xid="model" onLoad="modelLoad"
    onParamsReceive="modelParamsReceive"> 
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="schedulePickData" limit="20" confirmDelete="false" confirmRefresh="false"
      idColumn="schedule_pick_id" onCustomRefresh="schedulePickDataCustomRefresh"
      onAfterRefresh="schedulePickDataAfterRefresh" orderBy="orderid"> 
      <column isCalculate="true" label="操作" name="operate" type="String" xid="xid1"/>  
      <column label="编号" name="schedule_pick_id" type="String" xid="default45"/>  
      <column label="单据类型" name="bill_type" type="String" xid="default46"/>  
      <column label="企业编号" name="companyid" type="String" xid="default47"/>  
      <column label="单据编号" name="orderid" type="String" xid="default48"/>  
      <column label="领料日期" name="operate_time" type="Date" xid="default49"/>  
      <column label="经手人" name="operate_by" type="String" xid="default50"/>  
      <column label="仓库" name="houseid" type="String" xid="default51"/>  
      <column label="单位部门" name="customerid" type="String" xid="default52"/>  
      <column label="数量" name="count" type="Double" xid="default53"/>  
      <column label="总额" name="total" type="Double" xid="default55"/>  
      <column label="状态" name="status" type="String" xid="default56"/>  
      <column label="备注" name="remark" type="String" xid="default57"/>  
      <column label="打印次数" name="printing" type="Integer" xid="default58"/>  
      <column label="导出次数" name="outexcel" type="Integer" xid="default59"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default60"/>  
      <column label="创建人" name="create_by" type="String" xid="default61"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default62"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default63"/>  
      <column label="更新人" name="update_by" type="String" xid="default64"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default65"/>  
      <column label="原单号" name="originalbill" type="String" xid="default66"/>  
      <column label="属性列表" name="iproperty" type="String" xid="default67"/>  
      <column label="往来单位编号" name="customercode" type="String" xid="default68"/>  
      <column label="单位部门" name="customername" type="String" xid="default69"/>  
      <column label="仓库编号" name="housecode" type="String" xid="default70"/>  
      <column label="出库仓库" name="housename" type="String" xid="default71"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default72"/>  
      <column label="经手人" name="staffname" type="String" xid="default73"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="schedulePickDetailData" limit="999999" confirmDelete="false" confirmRefresh="false"
      idColumn="detailid" orderBy="goods_number:asc" onBeforeRefresh="schedulePickDetailDataBeforeRefresh"> 
      <column label="商品编号" name="codeid" type="String" xid="default74"/>  
      <column label="商品名称" name="itemname" type="String" xid="default75"/>  
      <column label="商品规格" name="sformat" type="String" xid="default76"/>  
      <column label="助记码" name="mcode" type="String" xid="default77"/>  
      <column label="商品分类" name="classid" type="String" xid="default82"/>  
      <column label="单位" name="unit" type="String" xid="default83"/>  
      <column label="图片" name="imgurl" type="String" xid="default84"/>  
      <column label="商品码" name="barcode" type="String" xid="default85"/>  
      <column label="属性1" name="property1" type="String" xid="default86"/>  
      <column label="属性2" name="property2" type="String" xid="default89"/>  
      <column label="属性3" name="property3" type="String" xid="default90"/>  
      <column label="属性4" name="property4" type="String" xid="default91"/>  
      <column label="属性5" name="property5" type="String" xid="default92"/>  
      <column label="商品分类" name="classname" type="String" xid="default93"/>  
      <column label="编号" name="detailid" type="String" xid="default94"/>  
      <column label="主表编号" name="schedule_pick_id" type="String" xid="default95"/>  
      <column label="序号" name="goods_number" type="Integer" xid="default96"/>  
      <column label="企业编号" name="companyid" type="String" xid="default97"/>  
      <column label="单据日期" name="operate_time" type="Date" xid="default98"/>  
      <column label="经手人" name="operate_by" type="String" xid="default99"/>  
      <column label="单据编号" name="orderid" type="String" xid="default100"/>  
      <column label="商品编号" name="itemid" type="String" xid="default101"/>  
      <column label="仓库" name="houseid" type="String" xid="default102"/>  
      <column label="单位部门" name="customerid" type="String" xid="default103"/>  
      <column label="类型" name="type" type="String" xid="default104"/>  
      <column label="数量" name="count" type="Double" xid="default105"/>  
      <column label="单价" name="price" type="Double" xid="default106"/>  
      <column label="金额" name="total" type="Double" xid="default107"/>  
      <column label="状态" name="status" type="String" xid="default108"/>  
      <column label="备注" name="remark" type="String" xid="default109"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default110"/>  
      <column label="创建人" name="create_by" type="String" xid="default111"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default112"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default113"/>  
      <column label="更新人" name="update_by" type="String" xid="default114"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default115"/>  
      <column label="原单号" name="originalbill" type="String" xid="default116"/>  
      <column label="批号" name="batchno" type="String" xid="default117"/>  
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
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="storehouseData" limit="-1" confirmDelete="false" confirmRefresh="false"
      idColumn="houseid" orderBy="status:asc,defaulthouse:desc,convert(housename using gbk):asc"> 
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
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="customerData" confirmDelete="false" confirmRefresh="false" limit="-1" orderBy="status:asc,convert(customername using gbk):asc"
      idColumn="customerid"> 
      <column label="编号" name="customerid" type="String" xid="default33"/>  
      <column label="企业编号" name="companyid" type="String" xid="default34"/>  
      <column label="客商编号" name="customercode" type="String" xid="default35"/>  
      <column label="客商名称" name="customername" type="String" xid="default36"/>  
      <column label="状态" name="status" type="String" xid="default54"/>  
      <column isCalculate="true" label="客商名称" name="name" type="String" xid="xid3"/>  
      <rule xid="rule1"> 
        <col name="name" xid="ruleCol1"> 
          <calculate xid="calculate1"> 
            <expr xid="default160">$row.val("customername") +( $row.val("status") =="2"?"-停用":"")</expr> 
          </calculate> 
        </col> 
      </rule> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="countData" idColumn="firstStatus"> 
      <column label="firstStatus" name="firstStatus" type="Integer" xid="xid2"/>  
      <column label="secondStatus" name="secondStatus" type="Integer" xid="xid18"/>  
      <data xid="default159">[{"firstStatus":1,"secondStatus":1}]</data> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="statusdata" idColumn="svalue"> 
      <column name="svalue" type="String" xid="column27"/>  
      <column name="slabel" type="String" xid="column27"/>  
      <data xid="default163">[{"svalue":"-1","slabel":"不显示作废"},{"svalue":"1","slabel":"已记帐"},{"svalue":"2","slabel":"已作废"},{"svalue":"0","slabel":"暂存"}]</data>
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="datetypedata" idColumn="svalue"> 
      <column name="svalue" type="String" xid="xid5"/>  
      <column name="slabel" type="String" xid="xid6"/>  
      <data xid="default162">[{"svalue":"operate_time","slabel":"入库日期"},{"svalue":"create_time","slabel":"制单日期"},{"svalue":"update_time","slabel":"更新日期"}]</data> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="fconditiondata" idColumn="id"> 
      <column name="id" type="String" xid="xid7"/>  
      <column name="houseid" type="String" xid="xid8"/>  
      <column name="housename" type="String" xid="xid9"/>  
      <column name="customid" type="String" xid="xid10"/>  
      <column name="customname" type="String" xid="xid11"/>  
      <column label="更多功能" name="hasmore" type="Integer" xid="xid14"/>  
      <data xid="default164">[{"id":"1","houseid":"","housename":"所有仓库","customid":"","customname":"所有供应商","hasmore":0}]</data> 
    </div> 
  </div>  
  <div component="$UI/system/components/justep/controlGroup/controlGroup" class="x-control-group gridoverflow"
    title="排产领料管理" xid="controlGroup1"> 
    <div class="x-control-group-title" xid="controlGroupTitle1"> 
      <span xid="span2">排产领料管理</span> 
    </div>  
    <div component="$UI/system/components/justep/toolBar/toolBar" class="x-toolbar form-inline x-toolbar-spliter"
      xid="toolBar1" bind-keypress="toolBar1Keypress"> 
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left custombtn oprbtn"
        label=" 新增" xid="add" onClick="addClick" icon="icon-android-add" style="display:none;"> 
        <i xid="i5" class="icon-android-add"/>  
        <span xid="span12">新增</span> 
      </a>  
      <select component="$UI/system/components/justep/select/select" class="form-control input-sm toolbarbtn input-frame"
        xid="statusselect" bind-options="statusdata" bind-optionsValue="svalue" bind-optionsLabel="slabel"
        bind-optionsCaption="全部状态" onChange="statusselectChange"/>  
      <div class="x-gridSelect x-gridSelect-sm selectgridbtn" component="$UI/system/components/justep/gridSelect/gridSelect"
        xid="housegridSelect" inputFilterable="true" bind-ref="$model.fconditiondata.ref(&quot;houseid&quot;)"
        bind-labelRef="$model.fconditiondata.ref(&quot;housename&quot;)"> 
        <option xid="option1" data="storehouseData" value="houseid" label="name"> 
          <columns xid="columns3"> 
            <column name="name" label="仓库名称" xid="column26"/> 
          </columns> 
        </option> 
      </div>  
      <div class="x-gridSelect x-gridSelect-sm x-edit selectgridbtn" component="$UI/system/components/justep/gridSelect/gridSelect"
        xid="customgridSelect" bind-ref="$model.fconditiondata.ref(&quot;customid&quot;)" inputFilterable="true"
        bind-labelRef="$model.fconditiondata.ref(&quot;customname&quot;)"> 
        <option xid="option2" data="customerData" value="customerid" label="name"> 
          <columns xid="columns2"> 
            <column name="customername" label="供应商" xid="column32"/> 
          </columns> 
        </option> 
      </div>  
      <select component="$UI/system/components/justep/select/select" class="form-control input-sm toolbarbtn input-frame"
        xid="datetypeselect" bind-options="datetypedata" bind-optionsValue="svalue"
        bind-optionsLabel="slabel"/>  
      <div class="dropdown btn-group" component="$UI/system/components/bootstrap/dropdown/dropdown"
        xid="daydropdown"> 
        <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-only-icon dropdown-toggle"
          icon="icon-arrow-down-b" xid="additemTogroupBtn" style="padding:5px 3px;margin-right:2px;margin-bottom:5px;border:1px solid #D2D6DE; color:#4F77AA;"
          bind-visible=" "> 
          <i class="icon-arrow-down-b" xid="i10"/>  
          <span xid="span19"/> 
        </a>  
        <ul component="$UI/system/components/justep/menu/menu" class="x-menu dropdown-menu"
          xid="groupmenu"/> 
      </div>  
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn input-frame"
        xid="begininput" dataType="Date" placeHolder="开始日期"/>  
      <span xid="span15">~</span>  
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn input-frame"
        xid="endinput" dataType="Date" placeHolder="结束日期"/>  
      <input component="$UI/system/components/justep/input/input" class="form-control input-sm toolbarbtn input-frame"
        xid="iteminput" dataType="String" placeHolder="商品信息,备注"/>  
      <input type="text" class="form-control input-sm toolbarbtn input-frame"
        component="$UI/system/components/justep/input/input" xid="searchcontent" placeHolder="查单号,经手人,制单人,备注"/>  
      <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-icon-left custombtn oprbtn"
        label=" 查找" xid="findBtn" id="otherinrefreshBtn" icon="icon-android-search"
        onClick="findBtnClick"> 
        <i xid="i7" class="icon-android-search"/>  
        <span xid="span13">查找</span> 
      </a>  
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left refeshbtn oprbtn"
        xid="scheduleRefreshBtn" label="刷新" icon="dataControl dataControl-refresh"
        onClick="scheduleRefreshBtnClick" id="schedulePickRefreshBtn"> 
        <i xid="i9" class="dataControl dataControl-refresh"/>  
        <span xid="span11">刷新</span> 
      </a>  
      <a component="$UI/system/components/justep/button/button" class="btn btn-sm btn-icon-left custombtn oprbtn"
        label=" 清除所有条件" xid="clearBtn" onClick="clearBtnClick" icon="dataControl dataControl-clear"> 
        <i xid="i2" class="dataControl dataControl-clear"/>  
        <span xid="span6">清除所有条件</span> 
      </a>  
      <div component="$UI/system/components/justep/button/buttonGroup" class="btn-group mygroup"
        tabbed="true" xid="buttonGroup1"> 
        <a component="$UI/system/components/justep/button/button" class="btn btn-link oprbtn"
          label=" 数据刷新" xid="refreshdatabtn" icon="dataControl dataControl-refresh"
          onClick="refreshdatabtnClick"> 
          <i xid="i8" class="dataControl dataControl-refresh"/>  
          <span xid="span18">数据刷新</span> 
        </a>  
        <a component="$UI/system/components/justep/button/button" class="btn btn-link oprbtn"
          label=" 导出汇总" xid="toexceltotal" onClick="toexceltotalClick" icon="linear linear-exitup"
          style="display:none;"> 
          <i xid="i3" class="linear linear-exitup"/>  
          <span xid="span16">导出汇总</span> 
        </a>  
        <a component="$UI/system/components/justep/button/button" class="btn btn-link oprbtn"
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
        xid="grid1" data="schedulePickData" multiselect="true" headerMenu="hideColumn,setColumn,groupColumn,saveLayout"
        moveColumn="true" directEdit="false" width="100%" showRowNumber="true" onCellRender="grid1CellRender"
        onRowClick="grid1RowClick" height="300" rowAttr="{style: val(&quot;status&quot;)==2?&quot;color:#AAA&quot;:&quot;color:#555&quot;}"
        useFooter="true" multiboxonly="true"> 
        <columns xid="columns1"> 
          <column name="operate" xid="column25" width="150" align="center"/>  
          <column width="50" name="status" xid="column18" align="center"/>  
          <column width="125" name="orderid" xid="column4" footerData="&quot;合计&quot;" align="center"/>  
          <column width="80" name="operate_time" xid="column5" align="center"/>  
          <column width="70" name="originalbill" xid="column1" align="center"/>  
          <column width="100" name="customername" xid="column13" align="center"/>  
          <column width="100" name="housename" xid="column14" align="center"/>  
          <column width="100" name="count" sorttype="float" xid="column15" footerData="$model.cutZero(($data.sum(&quot;count&quot;)).toFixed($model.countbit))"
            align="center"/>  
          <column width="100" name="total" sorttype="float" xid="column16" footerData="$model.cutZero(($data.sum(&quot;total&quot;)).toFixed($model.moneybit))"
            align="center"/>  
          <column width="60" name="staffname" xid="column34" align="center"/>  
          <column width="100" name="remark" xid="column17" align="center"/>  
          <column width="60" name="printing" xid="column19" align="center"/>  
          <column width="60" name="outexcel" xid="column20" align="center"/>  
          <column width="120" name="create_by" xid="column21" align="center"/>  
          <column width="140" name="create_time" xid="column22" align="center"/>  
          <column width="120" name="update_by" xid="column23" align="center"/>  
          <column width="140" name="update_time" xid="column24" align="center"/> 
        </columns> 
      </div>  
      <div component="$UI/system/components/justep/pagerBar/pagerBar" class="x-pagerbar container-fluid"
        xid="pagerBar1" data="schedulePickData" style="margin:0px;"> 
        <div class="row" xid="div1"> 
          <div class="col-sm-3" xid="div2"> 
            <div class="x-pagerbar-length" xid="div3"> 
              <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect"
                class="x-pagerlimitselect" xid="pagerLimitSelect1" defaultValue="10"> 
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
                xid="pagination1" data="schedulePickData"> 
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
        <span class="arrow-top" xid="span1"/> 
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
        altRows="true" class="x-grid-no-bordered x-grid-title-center gridoverflow "
        xid="grid2" height="350" data="schedulePickDetailData" width="100%" useFooter="true"
        onCellRender="grid2CellRender" serverSort="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout"> 
        <columns xid="columns4"> 
          <column width="40" name="goods_number" xid="column9" align="center"
            sorttype="int"/>  
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
          <column width="100" name="count" sorttype="float" xid="column6" footerData="$model.cutZero(($data.sum('count')).toFixed($model.countbit))"
            align="center"/>  
          <column width="80" name="counttounit1" xid="ucol1" align="center" sortable="false"
            hidden="true"/>  
          <column width="80" name="counttounit2" xid="ucol2" align="center" sortable="false"
            hidden="true"/>  
          <column width="80" name="counttounit3" xid="ucol3" align="center" sortable="false"
            hidden="true"/>  
          <column width="100" name="price" sorttype="float" xid="column7" align="center"/>  
          <column width="100" name="total" sorttype="float" xid="column7" footerData="$model.cutZero(($data.sum('total')).toFixed($model.moneybit))"
            align="center"/>  
          <column width="140" name="remark" xid="column8" align="center"/>  
          <column width="100" name="classname" xid="column31" align="center"/> 
        </columns> 
      </div> 
    </div> 
  </div>  
  <span component="$UI/system/components/justep/windowDialog/windowDialog" xid="pringDialog"
    status="normal" width="306mm" height="95%" forceRefreshOnOpen="true"/>
</div>

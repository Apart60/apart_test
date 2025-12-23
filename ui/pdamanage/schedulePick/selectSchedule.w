<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" component="$UI/system/components/justep/window/window" xid="window">  
  <div component="$UI/system/components/justep/model/model" xid="model" onLoad="modelLoad" onParamsReceive="modelParamsReceive"> 
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="scheduleData" idColumn="scheduleid" onCustomRefresh="scheduleDataCustomRefresh" confirmDelete="false" confirmRefresh="false">
      <column label="编号" name="scheduleid" type="String" xid="default154"/>  
      <column label="单据类型" name="bill_type" type="String" xid="default203"/>  
      <column label="原单号" name="originalbill" type="String" xid="default228"/>  
      <column label="企业编号" name="companyid" type="String" xid="default229"/>  
      <column label="单号" name="orderid" type="String" xid="default230"/>  
      <column label="排产日期" name="operate_time" type="Date" xid="default231"/>  
      <column label="排产员" name="operate_by" type="String" xid="default232"/>  
      <column label="数量" name="count" type="Double" xid="default233"/>  
      <column label="已入库数量" name="incount" type="Double" xid="default234"/>  
      <column label="备注" name="remark" type="String" xid="default235"/>  
      <column label="状态" name="status" type="String" xid="default236"/>  
      <column label="排产状态" name="schedulestatus" type="String" xid="default237"/>  
      <column label="打印次数" name="printing" type="Integer" xid="default238"/>  
      <column label="导出次数" name="outexcel" type="Integer" xid="default239"/>  
      <column label="创建人ID" name="create_id" type="String" xid="default240"/>  
      <column label="创建人" name="create_by" type="String" xid="default241"/>  
      <column label="创建时间" name="create_time" type="DateTime" xid="default242"/>  
      <column label="更新人ID" name="update_id" type="String" xid="default243"/>  
      <column label="更新人" name="update_by" type="String" xid="default244"/>  
      <column label="更新时间" name="update_time" type="DateTime" xid="default245"/>  
      <column label="属性列表" name="iproperty" type="String" xid="default246"/>  
      <column label="审核人ID" name="audit_id" type="String" xid="default247"/>  
      <column label="审核人" name="audit_by" type="String" xid="default248"/>  
      <column label="审核时间" name="audit_time" type="DateTime" xid="default249"/>  
      <column label="员工编号" name="staffcode" type="String" xid="default250"/>  
      <column label="排产员" name="staffname" type="String" xid="default251"/>  
      <column label="生产项目" name="project" type="String" xid="fdefault21"/>  
      <column label="生产部门" name="customerid" type="String" xid="fxid17"/>  
      <column isCalculate="true" label="生产部门" name="customername" type="String"
        xid="fxid22"/> 
    </div> 
  </div>  
  <div component="$UI/system/components/justep/panel/panel" class="x-panel x-full"
    xid="panel1"> 
    <div class="x-panel-top" xid="top1"> 
      <div component="$UI/system/components/justep/titleBar/titleBar" class="x-titlebar"
        xid="titleBar1" style="background-color:white;"> 
        <div class="x-titlebar-title flex4" xid="title1" style="text-align:left;color:#000;padding-left:10px;"><![CDATA[选择排产单]]></div>  
        <div class="x-titlebar-right reverse" xid="right1"> 
          <a component="$UI/system/components/justep/button/button" class="btn btn-sm x-dialog-button cancelbtn"
            label=" 关闭" xid="cancelbtn" onClick="cancelbtnClick" icon="icon-android-close"> 
            <i xid="i2" class="icon-android-close"/>  
            <span xid="span2">关闭</span> 
          </a> 
        </div> 
      </div> 
    </div>  
    <div class="x-panel-content" xid="content1"> 
      <div component="$UI/system/components/justep/toolBar/toolBar" class="x-toolbar form-inline x-toolbar-spliter"
        xid="toolBar1"> 
        <input type="text" class="form-control input-sm toolbarbtn" component="$UI/system/components/justep/input/input"
          xid="searchcontent" placeHolder="排产单号" style="margin-left:6px;"/>  
        <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left custombtn"
          label=" 查找" xid="findbtn" icon="icon-android-search" onClick="findBtnClick"> 
          <i xid="i7" class="icon-android-search"/>  
          <span xid="span13">查找</span> 
        </a>  
        <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left refeshbtn"
          onClick="refreshBtnClick" xid="refreshBtn" label=" 刷新" icon="dataControl dataControl-refresh"> 
          <i xid="i12" class="dataControl dataControl-refresh"/>  
          <span xid="span18">刷新</span> 
        </a> 
      </div>  
      <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
        altRows="true" class="x-grid-no-bordered x-grid-title-center gridoverflow"
        xid="grid1" data="scheduleData" multiselect="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout"
        directEdit="false" showRowNumber="true" useFooter="false" height="auto" width="100%"
        onCellRender="grid1CellRender" multiboxonly="true"> 
        <columns xid="columns1"> 
          <column width="100" name="operate" xid="column15" align="center" sortable="false" label="选择"/>  
          <column width="120" name="customername" xid="lcolumn92" align="center"/>  
          <column width="100" name="project" xid="lcolumn91" align="center"/>  
          <column width="125" name="orderid" xid="column2" align="center" label="排产单号"/>  
          <column width="100" name="operate_time" xid="column3" align="center"/>  
          <column width="80" name="staffname" xid="column41" label="排产员" align="center"/>  
          <column width="69" name="count" sorttype="float" xid="column5" align="center"></column><column width="120" name="remark" xid="column7" align="center"/>  
          <column width="80" name="outexcel" xid="column40" align="center"/>  
          <column width="80" name="printing" xid="column37" align="center"/>  
          <column width="120" name="create_by" xid="column10" align="center"/>  
          <column width="140" name="create_time" xid="column11" align="center"/>  
          <column width="120" name="update_by" xid="column12" align="center"/>  
          <column width="140" name="update_time" xid="column13" align="center"/>  
          <column width="120" name="audit_by" xid="column43" align="center"/>  
          <column width="140" name="audit_time" xid="column44" align="center"/> 
        </columns> 
      </div>  
      <div component="$UI/system/components/justep/pagerBar/pagerBar" class="x-pagerbar container-fluid"
        xid="pagerBar1" data="scheduleData" style="margin:0px;"> 
        <div class="row" xid="div1"> 
          <div class="col-sm-3" xid="div2"> 
            <div class="x-pagerbar-length" xid="div3"> 
              <label component="$UI/system/components/justep/pagerLimitSelect/pagerLimitSelect"
                class="x-pagerlimitselect" xid="pagerLimitSelect1"> 
                <span xid="span7">显示</span>  
                <select component="$UI/system/components/justep/select/select"
                  class="form-control input-sm" xid="select1"> 
                  <option value="10" xid="default118">10</option>  
                  <option value="20" xid="default119">20</option>  
                  <option value="50" xid="default120">50</option>  
                  <option value="100" xid="default121">100</option>  
                  <option value="200" xid="fdefault533">200</option> 
                </select>  
                <span xid="span8">条</span> 
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
                <li class="prev" xid="li3"> 
                  <a href="#" xid="a3"> 
                    <span aria-hidden="true" xid="span12">«</span>  
                    <span class="sr-only" xid="span17">Previous</span> 
                  </a> 
                </li>  
                <li class="next" xid="li4"> 
                  <a href="#" xid="a4"> 
                    <span aria-hidden="true" xid="span20">»</span>  
                    <span class="sr-only" xid="span21">Next</span> 
                  </a> 
                </li> 
              </ul> 
            </div> 
          </div> 
        </div> 
      </div> 
    </div>  
    <div class="x-panel-bottom" xid="bottom1"/> 
  </div> 
</div>

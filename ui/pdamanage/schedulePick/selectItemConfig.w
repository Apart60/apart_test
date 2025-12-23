<?xml version="1.0" encoding="utf-8"?>

<div xmlns="http://www.w3.org/1999/xhtml" xid="window" class="window" component="$UI/system/components/justep/window/window"
  design="device:pc">  
  <div component="$UI/system/components/justep/model/model" xid="model" onLoad="modelLoad"
    onParamsReceive="modelParamsReceive"> 
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="itemConfigData" confirmDelete="false" confirmRefresh="false" limit="9999"
      idColumn="batchno"> 
      <column label="批号" name="batchno" type="String" xid="xid1"/>  
      <column label="数量" name="count" type="Double" xid="xid2"/>  
      </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="false"
      xid="itemStockData" confirmDelete="false" confirmRefresh="false" limit="9999"
      idColumn="batchno" onBeforeRefresh="itemStockDataBeforeRefresh"> 
      <column name="batchno" type="String" xid="xid4"/>  
      <column name="count" type="Double" xid="xid5"/> 
    </div>  
    <div component="$UI/system/components/justep/data/data" autoLoad="true"
      xid="itemEditData" confirmDelete="false" confirmRefresh="false" limit="1" idColumn="id"> 
      <column name="id" type="String" xid="xid6"/>  
      <column label="批号" name="batchno" type="String" xid="xid7"/>  
      <column label="数量" name="count" type="Double" xid="xid8"/>  
      <column label="库存数" name="stockCount" type="String" xid="xid9"/>  
      <data xid="default1">[{"id":"1","batchno":"请先选择批号","count":0,"stockCount":"0"}]</data> 
    </div> 
  </div>  
  <div component="$UI/system/components/justep/panel/panel" class="x-panel x-full"
    xid="panel1"> 
    <div class="x-panel-top" xid="top3"> 
      <div component="$UI/system/components/justep/titleBar/titleBar" class="x-titlebar"
        xid="titleBar1" style="background-color:white;"> 
        <div class="x-titlebar-title flex4" xid="title1" style="text-align:left;color:#000;padding-left:10px;"><![CDATA[配置领料]]></div>  
        <div class="x-titlebar-right reverse" xid="right1"> 
          <a component="$UI/system/components/justep/button/button" class="btn btn-sm x-dialog-button cancelbtn"
            label=" 关闭" xid="cancelbtn" onClick="cancelbtnClick" icon="icon-android-close"> 
            <i xid="i2" class="icon-android-close"/>  
            <span xid="span2">关闭</span> 
          </a>  
          <a component="$UI/system/components/justep/button/button" class="btn btn-sm x-dialog-button cancelbtn"
            label="保存" xid="saveBtn" onClick="saveBtnClick"> 
            <i xid="i1"/>  
            <span xid="span1">保存</span> 
          </a> 
        </div> 
      </div> 
    </div>  
    <div class="x-panel-content" xid="content1"> 
      <div component="$UI/system/components/justep/toolBar/toolBar" class="x-toolbar form-inline x-toolbar-spliter"
        xid="toolBar1" style="margin-left:8px;"> 
        <div component="$UI/system/components/bootstrap/row/row" class="row"
          xid="row1"> 
          <div class="col col-xs-4" xid="col1"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit2"> 
              <label class="x-label" xid="label2"><![CDATA[批号：]]></label>  
              <div class="x-edit" xid="div2"> 
                <div class="x-gridSelect" component="$UI/system/components/justep/gridSelect/gridSelect"
                  xid="gridSelect1" onUpdateValue="gridSelect1UpdateValue" bind-ref='$model.itemEditData.ref("stockCount")'
                  bind-labelRef="$model.itemEditData.ref(&quot;batchno&quot;)"> 
                  <option xid="option1" data="itemStockData" value="count" label="batchno"
                    ext="count"/> 
                </div> 
              </div> 
            </div> 
          </div>  
          <div class="col col-xs-4" xid="col2"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit3"> 
              <label class="x-label" xid="label3"><![CDATA[数量：]]></label>  
              <input component="$UI/system/components/justep/input/input" class="form-control x-edit"
                xid="countInput" valueUpdateMode="keyup" dataType="Float" bind-blur="countInputBlur"
                bind-ref="$model.itemEditData.ref(&quot;count&quot;)"/> 
            </div> 
          </div>  
          <div class="col col-xs-4" xid="col3"> 
            <div component="$UI/system/components/justep/labelEdit/labelEdit"
              class="x-label-edit x-label30" xid="labelEdit4"> 
              <label class="x-label" xid="label4"><![CDATA[库存数：]]></label>  
              <div component="$UI/system/components/justep/output/output" class="x-output x-edit"
                xid="countOutput" bind-ref="$model.itemEditData.ref(&quot;stockCount&quot;)"/> 
            </div> 
          </div> 
        </div>  
        <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left custombtn"
          label="设置" xid="addBtn" icon="e-commerce e-commerce-jiahao" onClick="addBtnClick"> 
          <i xid="i7" class="e-commerce e-commerce-jiahao"/>  
          <span xid="span13">设置</span> 
        </a> 
      <a component="$UI/system/components/justep/button/button" class="btn  btn-sm btn-icon-left custombtn" label="全部清除" xid="clearBtn" icon="e-commerce e-commerce-jiahao" onClick="clearBtnClick">
   <i xid="i4" class="e-commerce e-commerce-jiahao"></i>
   <span xid="span4">全部清除</span></a></div>  
      <div component="$UI/system/components/justep/grid/grid" hiddenCaptionbar="true"
        altRows="true" class="x-grid-no-bordered x-grid-title-center gridoverflow"
        xid="grid1" data="itemConfigData" multiselect="false" moveColumn="true" headerMenu="hideColumn,setColumn,saveLayout"
        directEdit="false" showRowNumber="true" useFooter="true" height="600px" width="100%"
        onCellRender="grid1CellRender" multiboxonly="true"> 
        <columns xid="columns1"> 
          <column width="150" name="batchno" xid="column4" align="center"/>  
          <column width="150" name="count" xid="column5" align="center" footerData="$data.sum('count')"/> 
        </columns> 
      </div> 
    </div>  
    <div class="x-panel-bottom" xid="bottom1"/> 
  </div> 
</div>

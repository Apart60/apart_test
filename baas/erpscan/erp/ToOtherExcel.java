package erpscan.erp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Row;
import com.justep.baas.data.Table;

import erpscan.Common;
import erpscan.ToExcel;
import erpscan.excelopera;
import erpscan.save.Pdacommon;
import erpscan.utils.UnitUtil;

import java.net.URLEncoder;

public class ToOtherExcel {
	private static final String DATASOURCE = Common.DATASOURCE;

	// 导出生产领用汇总信息列表
	public static JSONObject getprodrequisitiontotalexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "prodrequisition_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");
		boolean showprice = new Boolean(request.getParameter("showprice"));

		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {
			String sqlstr = "select pr.*,  c.customercode,c.customername,s.staffcode,s.staffname,pr.worksheetbillno as billno,pr.worksheetitemid as itemid,o.itemname,o.sformat,o.codeid from prodrequisition pr  left join customer c on pr.customerid=c.customerid left join staffinfo s on pr.operate_by=s.staffid left join iteminfo o on pr.worksheetitemid=o.itemid where "
					+ condition + " order by pr.orderid asc ";

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 12);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 8);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 9, 12);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 10 + 184);
			hs.setColumnWidth(1, 256 * 14 + 184);
			hs.setColumnWidth(2, 256 * 15 + 184);
			hs.setColumnWidth(3, 256 * 12 + 184);
			hs.setColumnWidth(4, 256 * 10 + 184);
			hs.setColumnWidth(5, 256 * 10 + 184);
			hs.setColumnWidth(6, 256 * 10 + 184);
			hs.setColumnWidth(7, 256 * 6 + 184);
			hs.setColumnWidth(8, 256 * 10 + 184);
			hs.setColumnWidth(9, 256 * 12 + 184);
			hs.setColumnWidth(10, 256 * 16 + 184);
			hs.setColumnWidth(11, 256 * 6 + 184);
			hs.setColumnWidth(12, 256 * 12 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("领用日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("工单编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("领用部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("总额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(12);// 顺序创建
			hc.setCellValue("关联排产领料单");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;

			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);

			while (tabledata.getRows().size() > 0) {

				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(1);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(2);
					cell.setCellValue(info.getString("worksheetbillno") == null ? "" : info.getString("worksheetbillno"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(3);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(4);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(5);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);
					cell = hr.createCell(6);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);
					cell = hr.createCell(7);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(9);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(10);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(11);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(12);
					cell.setCellValue(info.getString("schedule_pick_billno"));
					cell.setCellStyle(hcs);
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					j++;
					ps.addBatch("update prodrequisition set outexcel=outexcel+1 where prodrequisitionid ='" + info.getString("prodrequisitionid") + "'");
					// datachageinfo = datachageinfo + (datachageinfo.equals("")
					// ?
					// "单据编号：" : "；") + info.getString("orderid");
				}
				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;

			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',23,'导出汇总','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}
			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 12; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 5) {
					hc.setCellValue(count);
				} else if (q == 6) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出生产领用明细信息列表
	public static JSONObject getprodrequisitiondetailexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "prodrequisitiondetail_all_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");
		boolean showprice = new Boolean(request.getParameter("showprice"));

		// 2020-12-21 多单位功能
		String unitsetdatastr = new String(request.getParameter("unitsetdata").getBytes("iso-8859-1"), "utf-8");
		JSONObject unitsetdata = JSONObject.parseObject(unitsetdatastr);

		String colnum = unitsetdata.getString("colnum");
		int countbit = unitsetdata.getInteger("countbit");

		String[] colnumarr = colnum.split(",");
		int unitcount = 0;
		if (!colnum.equals("")) {
			unitcount = colnumarr.length;
		}
		int unitcol = unitcount; // 有n个数量列需要换算，所有乘以系数n，这个数用于表头
		int stepnum = 0;

		Table itemproperty = null;
		int propertycount = 0;
		int k = 0;
		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {
			String sqlstr = " select i.codeid,i.itemname,i.sformat,i.mcode,i.classid,i.unit,i.imgurl,i.barcode,i.property1,i.property2,i.property3,i.property4,i.property5,i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3,ifnull(ic.classname,'') as classname,"
					+ "ifnull(sh.housecode,'') as housecode,ifnull(sh.housename,'') as housename,pr.*,ifnull(tor.order_id,'') as order_id,ifnull(tor.originalbill,'') as soriginalbill,ifnull(ibs.codeid,'') as pcodeid,ifnull(ibs.itemname,'') as pitemname,ifnull(ibs.sformat,'') as psformat,"
					+ "tor.salesorderid,tor.salesorderdetailid,s.staffcode,s.staffname,c.customername from prodrequisitiondetail pr left join  customer  c  on pr.customerid  =  c.customerid  left join storehouse sh on pr.houseid=sh.houseid left join t_order tor on pr.worksheetid=tor.id left join iteminfo ibs on tor.itemid=ibs.itemid left join staffinfo s on pr.operate_by=s.staffid  inner join  iteminfo i  on pr.itemid=i.itemid left "
					+ " join itemclass ic on i.classid=ic.classid  where " + condition + " order by pr.orderid asc ,pr.goods_number asc";

			itemproperty = excelopera.queryItemproperty(companyid, conn);
			propertycount = itemproperty.getRows().size();

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 25 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 25 + propertycount + unitcol - 3);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 25 + propertycount + unitcol - 2, 25 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 6 + 184);
			hs.setColumnWidth(1, 256 * 10 + 184);
			hs.setColumnWidth(2, 256 * 14 + 184);
			hs.setColumnWidth(3, 256 * 15 + 184);
			hs.setColumnWidth(4, 256 * 15 + 184);
			hs.setColumnWidth(5, 256 * 20 + 184);
			hs.setColumnWidth(6, 256 * 10 + 184);
			hs.setColumnWidth(7, 256 * 10 + 184);
			hs.setColumnWidth(8, 256 * 6 + 184);
			hs.setColumnWidth(9, 256 * 12 + 184);
			hs.setColumnWidth(10, 256 * 10 + 184);
			hs.setColumnWidth(11, 256 * 14 + 184);
			hs.setColumnWidth(12, 256 * 14 + 184);
			hs.setColumnWidth(13, 256 * 8 + 184);
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hs.setColumnWidth(14 + k, 256 * 8 + 184);
				}
			}
			stepnum = k;

			hs.setColumnWidth(14 + stepnum, 256 * 4 + 184);
			hs.setColumnWidth(15 + stepnum, 256 * 9 + 184);

			stepnum = UnitUtil.setUnitColumnWidth(hs, unitcount, 16, stepnum, 256 * 9 + 184);

			hs.setColumnWidth(16 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(17 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(18 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(19 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(20 + stepnum, 256 * 10 + 184);

			hs.setColumnWidth(21 + stepnum, 256 * 6 + 184);
			hs.setColumnWidth(22 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(23 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(24 + stepnum, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(24 + propertycount + unitcol - 2);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("领用日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("关联工单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("关联订单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("产品信息");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("领用部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("序号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("商品码");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("商品编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11);// 顺序创建
			hc.setCellValue("商品名称");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(12);// 顺序创建
			hc.setCellValue("商品规格");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(13);// 顺序创建
			hc.setCellValue("批号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			k = 0;
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hc = hr.createCell(14 + k);// 顺序创建
					hc.setCellValue(itemproperty.getRows().get(k).getString("propertyshow"));// 顺序塞入
					hc.setCellStyle(cellStyle3);// 居中
				}
			}
			stepnum = k;

			hc = hr.createCell(14 + stepnum);// 顺序创建
			hc.setCellValue("单位");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(15 + stepnum);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			stepnum = UnitUtil.setUnitColumnTag(hr, cellStyle3, unitsetdata, unitcount, 16, stepnum, "", colnumarr);

			hc = hr.createCell(16 + stepnum);// 顺序创建
			hc.setCellValue("单价");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(17 + stepnum);// 顺序创建
			hc.setCellValue("金额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(18 + stepnum);// 顺序创建
			hc.setCellValue("商品分类");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(19 + stepnum);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(20 + stepnum);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(21 + stepnum);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(22 + stepnum);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(23 + stepnum);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(24 + stepnum);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中
			
			hc = hr.createCell(25 + stepnum);// 顺序创建
			hc.setCellValue("关联排产领料单");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;

			int begin = j + 1;
			int end = j + 1;
			int currow = 0;
			String changeorderid = "";
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;

			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);

			while (tabledata.getRows().size() > 0) {

				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					currow = j + 1;

					if (!changeorderid.equals(info.getString("orderid"))) {
						// if (begin < end) {// 多条相同订单号信息进行合计
						// for (int col = 1; col <= 24 + propertycount +
						// unitcol; col++) {
						// if (col <= 5 || col >= 20 + propertycount + unitcol)
						// {
						// callRangeAddress24 = new CellRangeAddress(begin, end,
						// col, col);
						// hs.addMergedRegion(callRangeAddress24);
						// }
						// }
						// }
						changeorderid = info.getString("orderid");
						begin = currow;
						end = currow;

						ps.addBatch("update prodrequisition set outexcel=outexcel+1 where prodrequisitionid ='" + info.getString("prodrequisitionid") + "'");
						// datachageinfo = datachageinfo +
						// (datachageinfo.equals("")
						// ? "单据编号：" : "；") + info.getString("orderid");

					} else {
						end = currow;
					}

					hr = hs.createRow(currow);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(currow - 2);
					cell.setCellStyle(hcs);

					cell = hr.createCell(1);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(2);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(3);
					cell.setCellValue(info.getString("worksheetbillno") == null || info.getString("worksheetbillno").equals("null") || info.getString("worksheetbillno").equals("") ? "" : info
							.getString("worksheetbillno"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(4);
					cell.setCellValue(info.getString("order_id") + (info.getString("soriginalbill").equals("") ? "" : " (" + info.getString("soriginalbill") + ")"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(5);
					cell.setCellValue(info.getString("pcodeid") + " " + info.getString("pitemname") + " " + info.getString("psformat") + " "
							+ (info.getString("worksheetbatchno") == null ? "" : info.getString("worksheetbatchno")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(6);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(7);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(8);
					cell.setCellValue(Integer.parseInt(info.getValue("goods_number").toString()));
					cell.setCellStyle(hcs);

					cell = hr.createCell(9);
					cell.setCellValue(info.getString("barcode"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(10);
					cell.setCellValue(info.getString("codeid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(11);
					cell.setCellValue(info.getString("itemname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(12);
					cell.setCellValue(info.getString("sformat"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(13);
					cell.setCellValue(info.getString("batchno"));
					cell.setCellStyle(hcs);

					k = 0;
					if (propertycount > 0) {
						for (k = 0; k < propertycount; k++) {
							cell = hr.createCell(14 + k);
							cell.setCellValue(info.getString(itemproperty.getRows().get(k).getString("propertyname")));
							cell.setCellStyle(hcs);

						}
					}
					stepnum = k;

					cell = hr.createCell(14 + stepnum);
					cell.setCellValue(info.getString("unit"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(15 + stepnum);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);

					stepnum = UnitUtil.setUnitCellValue(hr, hcs, unitcount, 16, stepnum, colnumarr, "count", info, countbit);

					cell = hr.createCell(16 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("price").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(17 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(18 + stepnum);
					cell.setCellValue(info.getString("classname"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(19 + stepnum);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(20 + stepnum);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(21 + stepnum);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(22 + stepnum);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(23 + stepnum);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(24 + stepnum);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					
					cell = hr.createCell(25 + stepnum);
					cell.setCellValue(info.getString("schedule_pick_billno"));
					cell.setCellStyle(hcs);
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					j++;
				}
				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;
			// if (begin < end) {
			// for (int col = 1; col <= 24 + propertycount + unitcol; col++) {
			// if (col <= 5 || col >= 20 + propertycount + unitcol) {
			// callRangeAddress24 = new CellRangeAddress(begin, end, col, col);
			// hs.addMergedRegion(callRangeAddress24);
			// }
			// }
			// }
			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',23,'导出明细','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}

			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 25 + propertycount + unitcol; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 15 + propertycount) {
					hc.setCellValue(count);
				} else if (q == 17 + propertycount + unitcount) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出生产退料汇总信息列表
	public static JSONObject getprodrequisitionbacktotalexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "prodrequisition_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");
		boolean showprice = new Boolean(request.getParameter("showprice"));

		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {
			String sqlstr = "select pr.*, sh.housecode,sh.housename  ,c.customercode,c.customername,s.staffcode,s.staffname,pr.worksheetbillno as billno,pr.worksheetitemid as itemid,o.itemname,o.sformat,o.codeid from prodrequisition pr left join storehouse sh on pr.houseid=sh.houseid left join customer c on pr.customerid=c.customerid left join staffinfo s on pr.operate_by=s.staffid left join iteminfo o on pr.worksheetitemid=o.itemid where "
					+ condition + " order by pr.orderid asc ";

			// Table tabledata = ToExcel.querytabledata(tablename, conn,
			// condition, "main");
			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 11);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 8);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 9, 11);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 10 + 184);
			hs.setColumnWidth(1, 256 * 14 + 184);
			hs.setColumnWidth(2, 256 * 12 + 184);
			hs.setColumnWidth(3, 256 * 10 + 184);
			hs.setColumnWidth(4, 256 * 10 + 184);
			hs.setColumnWidth(5, 256 * 10 + 184);
			hs.setColumnWidth(6, 256 * 10 + 184);
			hs.setColumnWidth(7, 256 * 6 + 184);
			hs.setColumnWidth(8, 256 * 10 + 184);
			hs.setColumnWidth(9, 256 * 12 + 184);
			hs.setColumnWidth(10, 256 * 16 + 184);
			hs.setColumnWidth(11, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("退料日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("退料部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("总额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;

			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);

			while (tabledata.getRows().size() > 0) {
				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(1);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(2);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(3);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(4);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(5);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);
					cell = hr.createCell(6);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);
					cell = hr.createCell(7);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(9);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(10);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(11);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					j++;
					ps.addBatch("update prodrequisition set outexcel=outexcel+1 where prodrequisitionid ='" + info.getString("prodrequisitionid") + "'");
					// datachageinfo = datachageinfo + (datachageinfo.equals("")
					// ?
					// "单据编号：" : "；") + info.getString("orderid");
				}
				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;

			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',47,'导出汇总','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}
			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 11; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 5) {
					hc.setCellValue(count);
				} else if (q == 6) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出生产退料明细信息列表
	public static JSONObject getprodrequisitionbackdetailexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "prodrequisitiondetail_all_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");
		boolean showprice = new Boolean(request.getParameter("showprice"));

		// 2020-12-21 多单位功能
		String unitsetdatastr = new String(request.getParameter("unitsetdata").getBytes("iso-8859-1"), "utf-8");
		JSONObject unitsetdata = JSONObject.parseObject(unitsetdatastr);

		String colnum = unitsetdata.getString("colnum");
		int countbit = unitsetdata.getInteger("countbit");

		String[] colnumarr = colnum.split(",");
		int unitcount = 0;
		if (!colnum.equals("")) {
			unitcount = colnumarr.length;
		}
		int unitcol = unitcount; // 有n个数量列需要换算，所有乘以系数n，这个数用于表头
		int stepnum = 0;

		Table itemproperty = null;
		int propertycount = 0;
		int k = 0;
		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {
			String sqlstr = " select i.codeid,i.itemname,i.sformat,i.mcode,i.classid,i.unit,i.imgurl,i.barcode,i.property1,i.property2,i.property3,i.property4,i.property5,i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3,ifnull(ic.classname,'') as classname,"
					+ "sh.housecode,sh.housename,pr.*,s.staffcode,s.staffname,c.customername from prodrequisitiondetail pr left join  customer  c  on pr.customerid  =  c.customerid  left join storehouse sh on pr.houseid=sh.houseid   left join staffinfo s on pr.operate_by=s.staffid  inner join  iteminfo i  on pr.itemid=i.itemid left "
					+ " join itemclass ic on i.classid=ic.classid  where " + condition + " order by pr.orderid asc ,pr.goods_number asc";

			itemproperty = excelopera.queryItemproperty(companyid, conn);
			propertycount = itemproperty.getRows().size();

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 22 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 22 + propertycount + unitcol - 3);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 22 + propertycount + unitcol - 2, 22 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 6 + 184);
			hs.setColumnWidth(1, 256 * 10 + 184);
			hs.setColumnWidth(2, 256 * 14 + 184);
			hs.setColumnWidth(3, 256 * 10 + 184);
			hs.setColumnWidth(4, 256 * 10 + 184);
			hs.setColumnWidth(5, 256 * 6 + 184);
			hs.setColumnWidth(6, 256 * 15 + 184);
			hs.setColumnWidth(7, 256 * 12 + 184);
			hs.setColumnWidth(8, 256 * 10 + 184);
			hs.setColumnWidth(9, 256 * 14 + 184);
			hs.setColumnWidth(10, 256 * 14 + 184);
			hs.setColumnWidth(11, 256 * 8 + 184);
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hs.setColumnWidth(12 + k, 256 * 8 + 184);
				}
			}
			stepnum = k;

			hs.setColumnWidth(12 + stepnum, 256 * 4 + 184);
			hs.setColumnWidth(13 + stepnum, 256 * 9 + 184);

			stepnum = UnitUtil.setUnitColumnWidth(hs, unitcount, 14, stepnum, 256 * 9 + 184);

			hs.setColumnWidth(14 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(15 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(16 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(17 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(18 + stepnum, 256 * 10 + 184);

			hs.setColumnWidth(19 + stepnum, 256 * 6 + 184);
			hs.setColumnWidth(20 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(21 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(22 + stepnum, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(22 + propertycount + unitcol - 2);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("退料日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("退料部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("序号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("工单编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("商品码");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("商品编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("商品名称");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("商品规格");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11);// 顺序创建
			hc.setCellValue("批号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			k = 0;
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hc = hr.createCell(12 + k);// 顺序创建
					hc.setCellValue(itemproperty.getRows().get(k).getString("propertyshow"));// 顺序塞入
					hc.setCellStyle(cellStyle3);// 居中
				}
			}
			stepnum = k;

			hc = hr.createCell(12 + stepnum);// 顺序创建
			hc.setCellValue("单位");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(13 + stepnum);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			stepnum = UnitUtil.setUnitColumnTag(hr, cellStyle3, unitsetdata, unitcount, 14, stepnum, "", colnumarr);

			hc = hr.createCell(14 + stepnum);// 顺序创建
			hc.setCellValue("单价");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(15 + stepnum);// 顺序创建
			hc.setCellValue("金额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(16 + stepnum);// 顺序创建
			hc.setCellValue("商品分类");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(17 + stepnum);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(18 + stepnum);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(19 + stepnum);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(20 + stepnum);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(21 + stepnum);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(22 + stepnum);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;

			int begin = j + 1;
			int end = j + 1;
			int currow = 0;
			String changeorderid = "";
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;

			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);

			while (tabledata.getRows().size() > 0) {
				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					currow = j + 1;

					if (!changeorderid.equals(info.getString("orderid"))) {
						// if (begin < end) {// 多条相同订单号信息进行合计
						// for (int col = 1; col <= 22 + propertycount +
						// unitcol; col++) {
						// if (col <= 4 || col >= 18 + propertycount + unitcol)
						// {
						// callRangeAddress24 = new CellRangeAddress(begin, end,
						// col, col);
						// hs.addMergedRegion(callRangeAddress24);
						// }
						// }
						// }
						changeorderid = info.getString("orderid");
						begin = currow;
						end = currow;

						ps.addBatch("update prodrequisition set outexcel=outexcel+1 where prodrequisitionid ='" + info.getString("prodrequisitionid") + "'");
						// datachageinfo = datachageinfo +
						// (datachageinfo.equals("")
						// ? "单据编号：" : "；") + info.getString("orderid");

					} else {
						end = currow;
					}

					hr = hs.createRow(currow);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(currow - 2);
					cell.setCellStyle(hcs);

					cell = hr.createCell(1);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(2);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(3);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(4);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(5);
					cell.setCellValue(Integer.parseInt(info.getValue("goods_number").toString()));
					cell.setCellStyle(hcs);

					cell = hr.createCell(6);
					cell.setCellValue(info.getString("worksheetbillno") == null ? "" : info.getString("worksheetbillno"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(7);
					cell.setCellValue(info.getString("barcode"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("codeid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(9);
					cell.setCellValue(info.getString("itemname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(10);
					cell.setCellValue(info.getString("sformat"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(11);
					cell.setCellValue(info.getString("batchno"));
					cell.setCellStyle(hcs);

					k = 0;
					if (propertycount > 0) {
						for (k = 0; k < propertycount; k++) {
							cell = hr.createCell(12 + k);
							cell.setCellValue(info.getString(itemproperty.getRows().get(k).getString("propertyname")));
							cell.setCellStyle(hcs);

						}
					}
					stepnum = k;

					cell = hr.createCell(12 + stepnum);
					cell.setCellValue(info.getString("unit"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(13 + stepnum);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);

					stepnum = UnitUtil.setUnitCellValue(hr, hcs, unitcount, 14, stepnum, colnumarr, "count", info, countbit);

					cell = hr.createCell(14 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("price").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(15 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(16 + stepnum);
					cell.setCellValue(info.getString("classname"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(17 + stepnum);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(18 + stepnum);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(19 + stepnum);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(20 + stepnum);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(21 + stepnum);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(22 + stepnum);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					j++;
				}
				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;
			// if (begin < end) {
			// for (int col = 1; col <= 22 + propertycount + unitcol; col++) {
			// if (col <= 4 || col >= 18 + propertycount + unitcol) {
			// callRangeAddress24 = new CellRangeAddress(begin, end, col, col);
			// hs.addMergedRegion(callRangeAddress24);
			// }
			// }
			// }
			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',47,'导出明细','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}

			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 22 + propertycount + unitcol; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 13 + propertycount) {
					hc.setCellValue(count);
				} else if (q == 15 + propertycount + unitcount) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出其他入库汇总信息列表
	public static JSONObject getotherintotalexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String tablename = "otherinout_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");
		boolean showprice = new Boolean(request.getParameter("showprice"));

		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {

			String sqlstr = "select f.* from (select o.*,c.customercode AS customercode,c.customername AS customername,sh.housecode AS housecode,sh.housename AS housename,s.staffcode AS staffcode,s.staffname AS staffname from (((otherinout o left join customer c on((o.customerid = c.customerid))) left join storehouse sh on((o.houseid = sh.houseid))) left join staffinfo s on((o.operate_by = s.staffid)))  where o.companyid='"
					+ companyid + "' and o.bill_type='12') f where " + condition + " order by f.orderid asc";

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 11);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 8);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 9, 11);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 10 + 184);
			hs.setColumnWidth(1, 256 * 14 + 184);
			hs.setColumnWidth(2, 256 * 12 + 184);
			hs.setColumnWidth(3, 256 * 10 + 184);
			hs.setColumnWidth(4, 256 * 10 + 184);
			hs.setColumnWidth(5, 256 * 10 + 184);
			hs.setColumnWidth(6, 256 * 10 + 184);
			hs.setColumnWidth(7, 256 * 6 + 184);
			hs.setColumnWidth(8, 256 * 10 + 184);
			hs.setColumnWidth(9, 256 * 12 + 184);
			hs.setColumnWidth(10, 256 * 16 + 184);
			hs.setColumnWidth(11, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("入库日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("单位部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("入库仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("总额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;
			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);
			while (tabledata.getRows().size() > 0) {
				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(1);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(2);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(3);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(4);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(5);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);
					cell = hr.createCell(6);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						hc.setCellValue("--");
					}
					cell.setCellStyle(hcs);
					cell = hr.createCell(7);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(9);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(10);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(11);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					j++;

					ps.addBatch("update otherinout set outexcel=outexcel+1 where otherinoutid ='" + info.getString("otherinoutid") + "'");
					// datachageinfo = datachageinfo + (datachageinfo.equals("")
					// ?
					// "单据编号：" : "；") + info.getString("orderid");
				}

				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;

			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',12,'导出汇总','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}

			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 11; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 5) {
					hc.setCellValue(count);
				} else if (q == 6) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出其他入库明细信息列表
	public static JSONObject getotherindetailexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "otherinoutdetail_all_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");
		boolean showprice = new Boolean(request.getParameter("showprice"));

		// 2020-12-19
		String unitsetdatastr = new String(request.getParameter("unitsetdata").getBytes("iso-8859-1"), "utf-8");
		JSONObject unitsetdata = JSONObject.parseObject(unitsetdatastr);

		String colnum = unitsetdata.getString("colnum");
		int countbit = unitsetdata.getInteger("countbit");
		String[] colnumarr = colnum.split(",");
		int unitcount = 0;
		if (!colnum.equals("")) {
			unitcount = colnumarr.length;
		}
		int unitcol = unitcount; // 有n个数量列需要换算，所有乘以系数n，这个数用于表头

		int stepnum = 0;

		Table itemproperty = null;
		int propertycount = 0;
		int k = 0;
		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {// and o.bill_type='12'
			String sqlstr = "select f.* from (select o.*,im.codeid AS codeid,im.itemname AS itemname,im.sformat AS sformat,im.mcode AS mcode,im.classid AS classid,im.unit AS unit,im.imgurl AS imgurl,im.barcode AS barcode,im.property1 AS property1,im.property2 AS property2,im.property3 AS property3,im.property4 AS property4,im.property5 AS property5,im.unitstate1 AS unitstate1,im.unitset1 AS unitset1,im.unitstate2 AS unitstate2,im.unitset2 AS unitset2,im.unitstate3 AS unitstate3,im.unitset3 AS unitset3,ifnull(ic.classname,'') AS classname,c.customercode AS customercode,c.customername AS customername,sh.housecode AS housecode,sh.housename AS housename,s.staffcode AS staffcode,s.staffname AS staffname from  otherinoutdetail o left join customer c on o.customerid = c.customerid  left join storehouse sh on o.houseid = sh.houseid  left join staffinfo s on o.operate_by = s.staffid  left join  iteminfo im on  im.itemid = o.itemid  left join itemclass ic on im.classid = ic.classid  where  o.companyid='"
					+ companyid + "' and o.stype='121' ) f where " + condition + " order by f.orderid asc,f.goods_number asc";

			itemproperty = excelopera.queryItemproperty(companyid, conn);
			propertycount = itemproperty.getRows().size();

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 21 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 21 + propertycount + unitcol - 3);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 21 + propertycount + unitcol - 2, 21 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 6 + 184);
			hs.setColumnWidth(1, 256 * 10 + 184);
			hs.setColumnWidth(2, 256 * 14 + 184);
			hs.setColumnWidth(3, 256 * 12 + 184);
			hs.setColumnWidth(4, 256 * 6 + 184);
			hs.setColumnWidth(5, 256 * 12 + 184);
			hs.setColumnWidth(6, 256 * 10 + 184);
			hs.setColumnWidth(7, 256 * 14 + 184);
			hs.setColumnWidth(8, 256 * 14 + 184);
			hs.setColumnWidth(9, 256 * 8 + 184);
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hs.setColumnWidth(10 + k, 256 * 8 + 184);
				}
			}
			stepnum = k;

			hs.setColumnWidth(10 + stepnum, 256 * 4 + 184);
			hs.setColumnWidth(11 + stepnum, 256 * 9 + 184);

			stepnum = UnitUtil.setUnitColumnWidth(hs, unitcount, 12, stepnum, 256 * 9 + 184);

			hs.setColumnWidth(12 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(13 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(14 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(15 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(16 + stepnum, 256 * 10 + 184);
			hs.setColumnWidth(17 + stepnum, 256 * 8 + 184);
			hs.setColumnWidth(18 + stepnum, 256 * 6 + 184);
			hs.setColumnWidth(19 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(20 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(21 + stepnum, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());
			hc = hr.createCell(21 + propertycount + unitcol - 2);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("入库日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("单位部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("序号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("商品码");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("商品编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("商品名称");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("商品规格");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("批号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			k = 0;
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hc = hr.createCell(10 + k);// 顺序创建
					hc.setCellValue(itemproperty.getRows().get(k).getString("propertyshow"));// 顺序塞入
					hc.setCellStyle(cellStyle3);// 居中
				}
			}
			stepnum = k;

			hc = hr.createCell(10 + stepnum);// 顺序创建
			hc.setCellValue("单位");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11 + stepnum);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			stepnum = UnitUtil.setUnitColumnTag(hr, cellStyle3, unitsetdata, unitcount, 12, stepnum, "", colnumarr);

			hc = hr.createCell(12 + stepnum);// 顺序创建
			hc.setCellValue("单价");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(13 + stepnum);// 顺序创建
			hc.setCellValue("金额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(14 + stepnum);// 顺序创建
			hc.setCellValue("商品分类");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(15 + stepnum);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(16 + stepnum);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(17 + stepnum);// 顺序创建
			hc.setCellValue("入库仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(18 + stepnum);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(19 + stepnum);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(20 + stepnum);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(21 + stepnum);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;

			int begin = j + 1;
			int end = j + 1;
			int currow = 0;
			String changeorderid = "";
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;
			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);
			while (tabledata.getRows().size() > 0) {

				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					currow = j + 1;

					if (!changeorderid.equals(info.getString("orderid"))) {
						// if (begin < end) {// 多条相同订单号信息进行合计
						// for (int col = 1; col <= 21 + propertycount +
						// unitcol;
						// col++) {
						// if (col <= 3 || col >= 16 + propertycount + unitcol)
						// {
						// callRangeAddress24 = new CellRangeAddress(begin, end,
						// col, col);
						// hs.addMergedRegion(callRangeAddress24);
						// }
						// }
						// }
						changeorderid = info.getString("orderid");
						begin = currow;
						end = currow;

						ps.addBatch("update otherinout set outexcel=outexcel+1 where otherinoutid ='" + info.getString("otherinoutid") + "'");
						// datachageinfo = datachageinfo +
						// (datachageinfo.equals("")
						// ? "单据编号：" : "；") + info.getString("orderid");

					} else {
						end = currow;
					}

					hr = hs.createRow(currow);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(currow - 2);
					cell.setCellStyle(hcs);

					cell = hr.createCell(1);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(2);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(3);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(4);
					cell.setCellValue(Integer.parseInt(info.getValue("goods_number").toString()));
					cell.setCellStyle(hcs);

					cell = hr.createCell(5);
					cell.setCellValue(info.getString("barcode"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(6);
					cell.setCellValue(info.getString("codeid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(7);
					cell.setCellValue(info.getString("itemname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("sformat"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(9);
					cell.setCellValue(info.getString("batchno"));
					cell.setCellStyle(hcs);

					k = 0;
					if (propertycount > 0) {
						for (k = 0; k < propertycount; k++) {
							cell = hr.createCell(10 + k);
							cell.setCellValue(info.getString(itemproperty.getRows().get(k).getString("propertyname")));
							cell.setCellStyle(hcs);

						}
					}
					stepnum = k;

					cell = hr.createCell(10 + stepnum);
					cell.setCellValue(info.getString("unit"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(11 + stepnum);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);

					stepnum = UnitUtil.setUnitCellValue(hr, hcs, unitcount, 12, stepnum, colnumarr, "count", info, countbit);

					cell = hr.createCell(12 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("price").toString()));
					} else {
						hc.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(13 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						hc.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(14 + stepnum);
					cell.setCellValue(info.getString("classname"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(15 + stepnum);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(16 + stepnum);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(17 + stepnum);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(18 + stepnum);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(19 + stepnum);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(20 + stepnum);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(21 + stepnum);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					j++;
				}
				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;
			// if (begin < end) {
			// for (int col = 1; col <= 21 + propertycount + unitcol; col++) {
			// if (col <= 3 || col >= 16 + propertycount + unitcol) {
			// callRangeAddress24 = new CellRangeAddress(begin, end, col, col);
			// hs.addMergedRegion(callRangeAddress24);
			// }
			// }
			// }

			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',11,'导出明细','','共导出" + (j - 2) + "条记录，" + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}

			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 21 + propertycount + unitcol; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 11 + propertycount) {
					hc.setCellValue(count);
				} else if (q == 13 + propertycount + unitcount) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出其他出库汇总信息列表
	public static JSONObject getotherouttotalexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "otherinout_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");

		boolean showprice = new Boolean(request.getParameter("showprice"));

		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {
			String sqlstr = "select f.* from (select o.*,c.customercode AS customercode,c.customername AS customername,sh.housecode AS housecode,sh.housename AS housename,s.staffcode AS staffcode,s.staffname AS staffname from (((otherinout o left join customer c on((o.customerid = c.customerid))) left join storehouse sh on((o.houseid = sh.houseid))) left join staffinfo s on((o.operate_by = s.staffid)))  where o.companyid='"
					+ companyid + "' and o.bill_type='13') f where " + condition + " order by f.orderid asc";

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 11);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 8);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 9, 11);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 10 + 184);
			hs.setColumnWidth(1, 256 * 14 + 184);
			hs.setColumnWidth(2, 256 * 12 + 184);
			hs.setColumnWidth(3, 256 * 10 + 184);
			hs.setColumnWidth(4, 256 * 10 + 184);
			hs.setColumnWidth(5, 256 * 10 + 184);
			hs.setColumnWidth(6, 256 * 10 + 184);
			hs.setColumnWidth(7, 256 * 6 + 184);
			hs.setColumnWidth(8, 256 * 10 + 184);
			hs.setColumnWidth(9, 256 * 12 + 184);
			hs.setColumnWidth(10, 256 * 16 + 184);
			hs.setColumnWidth(11, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("出库日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("出库仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("单位部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("金额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(11);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;

			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);

			while (tabledata.getRows().size() > 0) {
				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(1);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(2);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(3);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(4);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(5);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);

					cell = hr.createCell(6);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						cell.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(7);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(9);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);
					cell = hr.createCell(10);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(11);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					j++;
					ps.addBatch("update otherinout set outexcel=outexcel+1 where otherinoutid ='" + info.getString("otherinoutid") + "'");
					// datachageinfo = datachageinfo + (datachageinfo.equals("")
					// ?
					// "单据编号：" : "；") + info.getString("orderid");
				}

				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;
			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',23,'导出汇总','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}
			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 11; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 5) {
					hc.setCellValue(count);
				} else if (q == 6) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

	// 导出其他出库明细信息列表
	public static JSONObject getotheroutdetailexcel(JSONObject params, ActionContext context) throws SQLException, NamingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);

		Connection conn = context.getConnection(DATASOURCE);

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
		String condition = new String(request.getParameter("condition").getBytes("iso-8859-1"), "utf-8");
		String loginuserid = request.getParameter("loginuserid");
		String loginUser = new String(request.getParameter("loginUser").getBytes("iso-8859-1"), "utf-8");
		String companyid = request.getParameter("companyid");
		String tablename = "otherinoutdetail_all_view";
		String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");
		String companyname = new String(request.getParameter("companyname").getBytes("iso-8859-1"), "utf-8");

		boolean showprice = new Boolean(request.getParameter("showprice"));

		// 2020-12-19
		String unitsetdatastr = new String(request.getParameter("unitsetdata").getBytes("iso-8859-1"), "utf-8");
		JSONObject unitsetdata = JSONObject.parseObject(unitsetdatastr);

		String colnum = unitsetdata.getString("colnum");
		int countbit = unitsetdata.getInteger("countbit");
		String[] colnumarr = colnum.split(",");
		int unitcount = 0;
		if (!colnum.equals("")) {
			unitcount = colnumarr.length;
		}
		int unitcol = unitcount; // 有n个数量列需要换算，所有乘以系数n，这个数用于表头

		int stepnum = 0;

		Table itemproperty = null;
		int propertycount = 0;
		int k = 0;
		SXSSFWorkbook hwb = new SXSSFWorkbook(100);// 第一步，创建一个workbook（一个excel文件）

		try {
			String sqlstr = "select f.* from (select o.*,im.codeid AS codeid,im.itemname AS itemname,im.sformat AS sformat,im.mcode AS mcode,im.classid AS classid,im.unit AS unit,im.imgurl AS imgurl,im.barcode AS barcode,im.property1 AS property1,im.property2 AS property2,im.property3 AS property3,im.property4 AS property4,im.property5 AS property5,im.unitstate1 AS unitstate1,im.unitset1 AS unitset1,im.unitstate2 AS unitstate2,im.unitset2 AS unitset2,im.unitstate3 AS unitstate3,im.unitset3 AS unitset3,ifnull(ic.classname,'') AS classname,c.customercode AS customercode,c.customername AS customername,sh.housecode AS housecode,sh.housename AS housename,s.staffcode AS staffcode,s.staffname AS staffname from  otherinoutdetail o left join customer c on o.customerid = c.customerid  left join storehouse sh on o.houseid = sh.houseid  left join staffinfo s on o.operate_by = s.staffid  left join  iteminfo im on  im.itemid = o.itemid  left join itemclass ic on im.classid = ic.classid  where  o.companyid='"
					+ companyid + "' and o.stype='131' ) f where " + condition + " order by f.orderid asc,f.goods_number asc";

			itemproperty = excelopera.queryItemproperty(companyid, conn);
			propertycount = itemproperty.getRows().size();

			Sheet hs = hwb.createSheet(filname);// 第二步，在workbook中添加一个sheet，对应excel文件中sheet
			org.apache.poi.ss.usermodel.Row hr = hs.createRow((int) 0);// 第三部，在sheet中添加表头第0行（相当于解释字段）

			hs.createFreezePane(0, 3, 0, 3);
			CellRangeAddress callRangeAddress24 = new CellRangeAddress(0, 0, 0, 21 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			callRangeAddress24 = new CellRangeAddress(1, 1, 0, 21 + propertycount + unitcol - 3);
			hs.addMergedRegion(callRangeAddress24);
			callRangeAddress24 = new CellRangeAddress(1, 1, 21 + propertycount + unitcol - 2, 21 + propertycount + unitcol);
			hs.addMergedRegion(callRangeAddress24);

			// hs.setDefaultColumnWidth(7);
			hs.setColumnWidth(0, 256 * 6 + 184);
			hs.setColumnWidth(1, 256 * 10 + 184);
			hs.setColumnWidth(2, 256 * 14 + 184);
			hs.setColumnWidth(3, 256 * 10 + 184);
			hs.setColumnWidth(4, 256 * 10 + 184);
			hs.setColumnWidth(5, 256 * 6 + 184);
			hs.setColumnWidth(6, 256 * 12 + 184);
			hs.setColumnWidth(7, 256 * 10 + 184);
			hs.setColumnWidth(8, 256 * 14 + 184);
			hs.setColumnWidth(9, 256 * 14 + 184);
			hs.setColumnWidth(10, 256 * 8 + 184);
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hs.setColumnWidth(11 + k, 256 * 8 + 184);
				}
			}
			stepnum = k;

			hs.setColumnWidth(11 + stepnum, 256 * 4 + 184);
			hs.setColumnWidth(12 + stepnum, 256 * 9 + 184);

			stepnum = UnitUtil.setUnitColumnWidth(hs, unitcount, 13, stepnum, 256 * 9 + 184);

			hs.setColumnWidth(13 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(14 + stepnum, 256 * 9 + 184);

			hs.setColumnWidth(15 + stepnum, 256 * 9 + 184);
			hs.setColumnWidth(16 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(17 + stepnum, 256 * 10 + 184);

			hs.setColumnWidth(18 + stepnum, 256 * 6 + 184);
			hs.setColumnWidth(19 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(20 + stepnum, 256 * 12 + 184);
			hs.setColumnWidth(21 + stepnum, 256 * 6 + 184);

			// 行内容字体
			Font font = hwb.createFont();
			CellStyle hcs = hwb.createCellStyle();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("MS Sans Serif");
			hcs.setFont(font);
			hcs.setAlignment(HorizontalAlignment.CENTER);
			hcs.setVerticalAlignment(VerticalAlignment.CENTER);
			hcs.setBorderBottom(BorderStyle.THIN);
			hcs.setBorderLeft(BorderStyle.THIN);
			hcs.setBorderRight(BorderStyle.THIN);
			hcs.setBorderTop(BorderStyle.THIN);
			hcs.setWrapText(true);

			// 大标题内容字体
			Font font2 = hwb.createFont();
			CellStyle cellStyle20 = hwb.createCellStyle();
			font2.setBold(true);
			font2.setFontHeightInPoints((short) 16);
			font2.setFontName("宋体");
			cellStyle20.setFont(font2);
			cellStyle20.setAlignment(HorizontalAlignment.CENTER);
			cellStyle20.setVerticalAlignment(VerticalAlignment.CENTER);

			// 标题内容字体
			Font font3 = hwb.createFont();
			CellStyle cellStyle3 = hwb.createCellStyle();
			font3.setFontHeightInPoints((short) 10);
			font3.setBold(true);
			font3.setFontName("宋体");
			cellStyle3.setFont(font3);
			cellStyle3.setAlignment(HorizontalAlignment.CENTER);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle3.setBorderBottom(BorderStyle.THIN);
			cellStyle3.setBorderLeft(BorderStyle.THIN);
			cellStyle3.setBorderRight(BorderStyle.THIN);
			cellStyle3.setBorderTop(BorderStyle.THIN);
			cellStyle3.setWrapText(true); // 换行

			CellStyle cellStyleleft = hwb.createCellStyle();
			cellStyleleft.setFont(font);
			cellStyleleft.setAlignment(HorizontalAlignment.LEFT);
			cellStyleleft.setVerticalAlignment(VerticalAlignment.CENTER);

			CellStyle cellStyleright = hwb.createCellStyle();
			cellStyleright.setFont(font);
			cellStyleright.setAlignment(HorizontalAlignment.RIGHT);
			cellStyleright.setVerticalAlignment(VerticalAlignment.CENTER);

			hr = hs.createRow((int) 0);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (15 * 40));
			Cell hc = hr.createCell(0);
			hc.setCellValue(companyname + "--" + filname);
			hc.setCellStyle(cellStyle20);

			hr = hs.createRow((int) 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (10 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue(datastr);
			hc.setCellStyle(cellStyleleft);

			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(new Date());

			hc = hr.createCell(21 + propertycount + unitcol - 2);// 顺序创建
			hc.setCellValue("导出时间：" + str);
			hc.setCellStyle(cellStyleright);

			hr = hs.createRow((int) 2);
			hr.setHeight((short) (12 * 40));
			hc = hr.createCell(0);// 顺序创建
			hc.setCellValue("编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(1);// 顺序创建
			hc.setCellValue("出库日期");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(2);// 顺序创建
			hc.setCellValue("单据编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(3);// 顺序创建
			hc.setCellValue("出库仓库");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(4);// 顺序创建
			hc.setCellValue("单位部门");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(5);// 顺序创建
			hc.setCellValue("序号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(6);// 顺序创建
			hc.setCellValue("商品码");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(7);// 顺序创建
			hc.setCellValue("商品编号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(8);// 顺序创建
			hc.setCellValue("商品名称");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(9);// 顺序创建
			hc.setCellValue("商品规格");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(10);// 顺序创建
			hc.setCellValue("批号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			k = 0;
			if (propertycount > 0) {
				for (k = 0; k < propertycount; k++) {
					hc = hr.createCell(11 + k);// 顺序创建
					hc.setCellValue(itemproperty.getRows().get(k).getString("propertyshow"));// 顺序塞入
					hc.setCellStyle(cellStyle3);// 居中
				}
			}
			stepnum = k;

			hc = hr.createCell(11 + stepnum);// 顺序创建
			hc.setCellValue("单位");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(12 + stepnum);// 顺序创建
			hc.setCellValue("数量");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			stepnum = UnitUtil.setUnitColumnTag(hr, cellStyle3, unitsetdata, unitcount, 13, stepnum, "", colnumarr);

			hc = hr.createCell(13 + stepnum);// 顺序创建
			hc.setCellValue("单价");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(14 + stepnum);// 顺序创建
			hc.setCellValue("金额");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(15 + stepnum);// 顺序创建
			hc.setCellValue("商品分类");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(16 + stepnum);// 顺序创建
			hc.setCellValue("备注");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(17 + stepnum);// 顺序创建
			hc.setCellValue("原单号");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(18 + stepnum);// 顺序创建
			hc.setCellValue("经手人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(19 + stepnum);// 顺序创建
			hc.setCellValue("制单人");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(20 + stepnum);// 顺序创建
			hc.setCellValue("制单时间");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			hc = hr.createCell(21 + stepnum);// 顺序创建
			hc.setCellValue("状态");// 顺序塞入
			hc.setCellStyle(cellStyle3);// 居中

			int j = 2;
			double count = 0;
			double total = 0;

			int begin = j + 1;
			int end = j + 1;
			int currow = 0;
			String changeorderid = "";
			Statement ps = conn.createStatement();
			String datachageinfo = "";

			int offset = 0;
			int limit = Common.Exportlimit;

			Table tabledata = DataUtils.queryData(conn, sqlstr + " limit " + offset + "," + limit, null, null, null, null);

			while (tabledata.getRows().size() > 0) {

				Iterator<Row> iteratordata = tabledata.getRows().iterator();
				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					currow = j + 1;

					if (!changeorderid.equals(info.getString("orderid"))) {
						// if (begin < end) {// 多条相同订单号信息进行合计
						// for (int col = 1; col <= 21 + propertycount +
						// unitcol;
						// col++) {
						// if (col <= 4 || col >= 17 + propertycount + unitcol)
						// {
						// callRangeAddress24 = new CellRangeAddress(begin, end,
						// col, col);
						// hs.addMergedRegion(callRangeAddress24);
						// }
						// }
						// }
						changeorderid = info.getString("orderid");
						begin = currow;
						end = currow;

						ps.addBatch("update otherinout set outexcel=outexcel+1 where otherinoutid ='" + info.getString("otherinoutid") + "'");
						// datachageinfo = datachageinfo +
						// (datachageinfo.equals("")
						// ? "单据编号：" : "；") + info.getString("orderid");

					} else {
						end = currow;
					}

					hr = hs.createRow(currow);// 在sheet中自动随 j+1 增加一行（j 是表头）
					hr.setHeight((short) (13 * 40));
					Cell cell = hr.createCell(0);
					cell.setCellValue(currow - 2);
					cell.setCellStyle(hcs);

					cell = hr.createCell(1);
					cell.setCellValue(sdfdate.format(info.getDate("operate_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(2);
					cell.setCellValue(info.getString("orderid"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(3);
					cell.setCellValue(info.getString("housename"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(4);
					cell.setCellValue(info.getString("customername"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(5);
					cell.setCellValue(Integer.parseInt(info.getValue("goods_number").toString()));
					cell.setCellStyle(hcs);

					cell = hr.createCell(6);
					cell.setCellValue(info.getString("barcode"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(7);
					cell.setCellValue(info.getString("codeid"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(8);
					cell.setCellValue(info.getString("itemname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(9);
					cell.setCellValue(info.getString("sformat"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(10);
					cell.setCellValue(info.getString("batchno"));
					cell.setCellStyle(hcs);

					k = 0;
					if (propertycount > 0) {
						for (k = 0; k < propertycount; k++) {
							cell = hr.createCell(11 + k);
							cell.setCellValue(info.getString(itemproperty.getRows().get(k).getString("propertyname")));
							cell.setCellStyle(hcs);

						}
					}
					stepnum = k;

					cell = hr.createCell(11 + stepnum);
					cell.setCellValue(info.getString("unit"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(12 + stepnum);
					cell.setCellValue(Double.parseDouble(info.getValue("count").toString()));
					cell.setCellStyle(hcs);

					stepnum = UnitUtil.setUnitCellValue(hr, hcs, unitcount, 13, stepnum, colnumarr, "count", info, countbit);

					cell = hr.createCell(13 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("price").toString()));
					} else {
						hc.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(14 + stepnum);
					if (showprice) {
						cell.setCellValue(Double.parseDouble(info.getValue("total").toString()));
					} else {
						hc.setCellValue("--");
					}
					cell.setCellStyle(hcs);

					cell = hr.createCell(15 + stepnum);
					cell.setCellValue(info.getString("classname"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(16 + stepnum);
					cell.setCellValue(info.getString("remark"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(17 + stepnum);
					cell.setCellValue(info.getString("originalbill"));
					cell.setCellStyle(hcs);

					cell = hr.createCell(18 + stepnum);
					cell.setCellValue(info.getString("staffname"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(19 + stepnum);
					cell.setCellValue(info.getString("create_by"));
					cell.setCellStyle(hcs);
					cell = hr.createCell(20 + stepnum);
					cell.setCellValue(sdf.format(info.getDateTime("create_time")));
					cell.setCellStyle(hcs);

					cell = hr.createCell(21 + stepnum);
					cell.setCellValue(Pdacommon.getStatus(info.getString("status")));
					cell.setCellStyle(hcs);
					total = Pdacommon.adddouble(total, Double.parseDouble(info.getValue("total").toString()));
					count = Pdacommon.adddouble(count, Double.parseDouble(info.getValue("count").toString()));
					j++;
				}
				iteratordata = null;

				if (tabledata.getRows().size() < limit)
					break;

				offset++;
				tabledata = DataUtils.queryData(conn, sqlstr + " limit " + (offset * limit) + "," + limit, null, null, null, null);

			}
			tabledata = null;
			// if (begin < end) {
			// for (int col = 1; col <= 21 + propertycount + unitcol; col++) {
			// if (col <= 4 || col >= 17 + propertycount + unitcol) {
			// callRangeAddress24 = new CellRangeAddress(begin, end, col, col);
			// hs.addMergedRegion(callRangeAddress24);
			// }
			// }
			// }
			if (j - 2 > 0) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',23,'导出明细','','共导出" + (j - 2) + "条记录 " + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
			}

			hr = hs.createRow((int) j + 1);// 在sheet中自动随 j+1 增加一行（j 是表头）
			hr.setHeight((short) (12 * 40));

			for (int q = 0; q <= 21 + propertycount + unitcol; q++) {

				hc = hr.createCell(q);// 顺序创建
				if (q == 0) {
					hc.setCellValue("合计");
				} else if (q == 12 + propertycount) {
					hc.setCellValue(count);
				} else if (q == 14 + propertycount) {
					if (showprice) {
						hc.setCellValue(total);
					} else {
						hc.setCellValue("--");
					}
				} else {
					hc.setCellValue("");
				}
				hc.setCellStyle(cellStyle3);
			}

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			hwb.write(os);
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			resp.reset();
			// resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			// resp.setHeader("Content-Disposition", "attachment;filename=" +
			// new String((filname + (new
			// SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) +
			// ".xlsx").getBytes(), "iso-8859-1"));

			resp.setContentType("application/octet-stream;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(filname, "utf-8") + (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".xlsx");// new
																																																// String((filname
																																																// +
																																																// (new
																																																// SimpleDateFormat("yyyyMMddHHmmss")).format(new
																																																// Date())
																																																// +
																																																// ".xlsx").getBytes(),
																																																// "utf-8")

			ServletOutputStream out = resp.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
				bos.flush();
			}
			hwb.dispose();
			out.close();
			os.close();
			is.close();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
		} finally {
			conn.close();
			if (hwb != null) {
				hwb.dispose();
			}
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			System.gc();
			System.runFinalization();
		}
		return null;
	}

}

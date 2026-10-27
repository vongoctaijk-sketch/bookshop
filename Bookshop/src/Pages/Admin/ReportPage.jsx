import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Card, DatePicker, Statistic, Table } from "antd";
import dayjs from "dayjs";
import adminReportService from "../../services/AdminReportService";

const money = (value) => Number(value || 0).toLocaleString("vi-VN", { style: "currency", currency: "VND" });
const pointColumns = [
  { title: "Kỳ", dataIndex: "period", key: "period" },
  { title: "Doanh thu", dataIndex: "revenue", key: "revenue", render: money },
];
const bookColumns = [
  { title: "Sách", dataIndex: "title", key: "title" },
  { title: "Số lượng", dataIndex: "quantity", key: "quantity" },
  { title: "Doanh thu", dataIndex: "revenue", key: "revenue", render: money },
];

export default function ReportPage() {
  const [range, setRange] = useState([dayjs().startOf("month"), dayjs()]);
  const query = useQuery({
    queryKey: ["admin-report", range[0]?.format("YYYY-MM-DD"), range[1]?.format("YYYY-MM-DD")],
    queryFn: () => adminReportService.get({ from: range[0]?.format("YYYY-MM-DD"), to: range[1]?.format("YYYY-MM-DD") }),
  });
  const report = query.data || {};
  return (
    <div className="space-y-5">
      <Card title="Báo cáo doanh thu" extra={<DatePicker.RangePicker value={range} onChange={(value) => value && setRange(value)} />}>
        <div className="grid grid-cols-2 gap-4 lg:grid-cols-5">
          <Statistic title="Doanh thu kỳ" value={money(report.totalRevenue)} />
          <Statistic title="Doanh thu hôm nay" value={money(report.todayRevenue)} />
          <Statistic title="Đơn hoàn thành" value={report.completedOrders || 0} />
          <Statistic title="Sách đã bán" value={report.unitsSold || 0} />
          <Statistic title="Giá trị đơn TB" value={money(report.averageOrderValue)} />
        </div>
      </Card>
      <div className="grid gap-5 lg:grid-cols-2">
        <Card title="Doanh thu theo ngày"><Table rowKey="period" size="small" loading={query.isLoading} columns={pointColumns} dataSource={report.revenueByDay || []} pagination={{ pageSize: 7 }} /></Card>
        <Card title="Doanh thu theo tháng"><Table rowKey="period" size="small" loading={query.isLoading} columns={pointColumns} dataSource={report.revenueByMonth || []} pagination={false} /></Card>
        <Card title="Doanh thu theo năm"><Table rowKey="period" size="small" loading={query.isLoading} columns={pointColumns} dataSource={report.revenueByYear || []} pagination={false} /></Card>
        <Card title="Doanh thu theo thể loại"><Table rowKey="category" size="small" loading={query.isLoading} columns={[{ title: "Thể loại", dataIndex: "category", key: "category" }, { title: "Doanh thu", dataIndex: "revenue", key: "revenue", render: money }]} dataSource={report.revenueByCategory || []} pagination={false} /></Card>
        <Card title="Sách bán chạy"><Table rowKey="bookId" size="small" columns={bookColumns} dataSource={report.bestSellers || []} pagination={false} /></Card>
        <Card title="Tồn kho thấp"><Table rowKey="bookId" size="small" columns={bookColumns} dataSource={report.lowestStock || []} pagination={false} /></Card>
      </div>
      <Card title="Tồn kho cao"><Table rowKey="bookId" size="small" columns={bookColumns} dataSource={report.highestStock || []} pagination={false} /></Card>
    </div>
  );
}

import { Table } from "antd";

function CommonTable({
  columns,
  dataSource,
  loading,
  pagination,
  onChange,
  rowKey = "id",
}) {
  return (
    <Table
      rowKey={rowKey}
      columns={columns}
      dataSource={dataSource}
      loading={loading}
      pagination={pagination}
      onChange={onChange}
    />
  );
}

export default CommonTable;

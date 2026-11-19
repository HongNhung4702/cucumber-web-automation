
@lecturers @lecturer-invalid-search
Feature: Tra cứu thời khóa biểu giảng viên với thông tin không hợp lệ

  @TC_39 @negative
  Scenario: Tra cứu giảng viên không tồn tại
    When người dùng tra cứu giảng viên với từ khóa không hợp lệ TC_39
    Then bộ lọc giảng viên TC_39 giữ đúng dữ liệu đã chọn
    And TC_39 không có lịch giảng dạy và không hiển thị lỗi

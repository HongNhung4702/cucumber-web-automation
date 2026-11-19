
@lecturers @lecturer-reset
Feature: Làm mới điều kiện tra cứu giảng viên

  @TC_40 @reset
  Scenario: Khôi phục bộ lọc về trạng thái mặc định
    When người dùng tra cứu lịch giảng viên TC_40
    And người dùng nhấn Làm mới TC_40
    Then bộ lọc TC_40 trở về giá trị mặc định
    And bảng lịch TC_40 trở về trạng thái rỗng

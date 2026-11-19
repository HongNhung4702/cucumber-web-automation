
@lecturers @lecturer-consecutive-search
Feature: Tra cứu thời khóa biểu giảng viên liên tiếp

  @TC_42 @filter @baseline
  Scenario: Tra cứu A rồi B rồi quay lại A
    When người dùng tra cứu bộ lọc A TC_42
    And người dùng tra cứu bộ lọc B TC_42
    Then người dùng quay lại A TC_42 và dữ liệu không thay đổi


@lecturers @lecturer-search-again
Feature: Kiểm tra tra cứu lại sau khi làm mới điều kiện

  @TC_41 @reset @baseline
  Scenario: Kết quả tra cứu lại sau Làm mới không thay đổi
    When người dùng tra cứu giảng viên lần đầu TC_41
    And người dùng làm mới điều kiện TC_41
    Then tra cứu lại TC_41 cho kết quả giống lần đầu và baseline

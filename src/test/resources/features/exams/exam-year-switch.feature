
@student @exam @exam-year-switch
Feature: Kiểm thử thay đổi năm học khi xem lịch thi
  Là sinh viên đã đăng nhập
  Tôi muốn thay đổi năm học khi tra cứu lịch thi
  Để xem đúng dữ liệu lịch thi của năm học đã chọn

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_29 @filter @data-verification
  Scenario: Danh sách lịch thi cập nhật khi thay đổi năm học
    When sinh viên mở lịch thi năm ban đầu TC_29
    Then lịch thi năm ban đầu TC_29 khớp baseline

    When sinh viên đổi năm học lịch thi sang 2022-2023 TC_29
    Then lịch thi năm mới TC_29 hiển thị đúng bộ lọc và baseline

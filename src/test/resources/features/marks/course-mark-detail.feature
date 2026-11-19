
@student @marks @course-mark-detail
Feature: Kiểm thử xem điểm chi tiết học phần
  Là sinh viên đã đăng nhập
  Tôi muốn xem chi tiết điểm học phần Toán logic
  Để kiểm tra các thành phần điểm và điểm tổng hợp

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_35 @data-verification
  Scenario: Mở và đóng chi tiết điểm học phần Toán logic
    When sinh viên mở chi tiết điểm Toán logic TC_35
    Then bảng chi tiết điểm Toán logic TC_35 khớp baseline
    And đóng chi tiết TC_35 không làm thay đổi điểm tổng hợp


@student @weekly-navigation
Feature: Kiểm thử điều hướng tuần thời khóa biểu sinh viên
  Là sinh viên đã đăng nhập
  Tôi muốn chuyển tuần tiếp theo và quay lại tuần trước
  Để kiểm tra lịch học được cập nhật và khôi phục đúng

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_24 @navigation @data-verification
  Scenario: Chuyển sang tuần tiếp theo rồi quay lại tuần ban đầu
    When sinh viên mở và ghi nhận tuần ban đầu TC_24
    Then lịch tuần ban đầu TC_24 khớp baseline

    When sinh viên chuyển sang tuần tiếp theo TC_24
    Then tuần tiếp theo TC_24 tăng đúng bảy ngày và khớp baseline

    When sinh viên quay lại tuần trước TC_24
    Then tuần ban đầu TC_24 được khôi phục đúng dữ liệu

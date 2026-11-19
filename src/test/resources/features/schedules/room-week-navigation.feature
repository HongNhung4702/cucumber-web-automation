
@student @room-week-navigation
Feature: Kiểm thử điều hướng tuần thời khóa biểu theo phòng
  Là sinh viên đã đăng nhập
  Tôi muốn chuyển sang tuần tiếp theo và quay lại tuần trước
  Để xác nhận lịch theo phòng cập nhật và khôi phục đúng

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_25 @navigation @data-verification
  Scenario: Chuyển tuần trong thời khóa biểu theo phòng rồi quay lại
    When sinh viên mở và ghi nhận lịch phòng tuần ban đầu TC_25

    When sinh viên chuyển sang tuần tiếp theo của lịch phòng TC_25
    Then lịch phòng tuần tiếp theo TC_25 tăng bảy ngày và khớp baseline

    When sinh viên quay lại tuần trước của lịch phòng TC_25
    Then lịch phòng tuần ban đầu TC_25 được khôi phục

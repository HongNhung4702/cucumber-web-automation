
@student @room-weekly-schedule
Feature: Kiểm thử thời khóa biểu theo phòng học
  Là sinh viên đã đăng nhập
  Tôi muốn xem thời khóa biểu theo phòng
  Để kiểm tra lịch sử dụng phòng trong tuần đã chọn

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_23 @ui
  Scenario: Hiển thị thời khóa biểu theo phòng đúng bộ lọc
    When sinh viên mở thời khóa biểu theo phòng TC_23
    Then bộ lọc thời khóa biểu phòng TC_23 hiển thị đúng
    And bảng thời khóa biểu phòng TC_23 có đúng ngày và phòng

  @TC_23 @data-verification
  Scenario: Đối chiếu dữ liệu thời khóa biểu theo phòng
    When sinh viên mở thời khóa biểu theo phòng TC_23
    Then thời khóa biểu theo phòng TC_23 khớp baseline

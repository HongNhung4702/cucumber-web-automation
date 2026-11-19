
@student @student-schedule-print
Feature: Kiểm thử bản xem trước thời khóa biểu sinh viên
  Là sinh viên đã đăng nhập
  Tôi muốn xem trước thời khóa biểu để in
  Để kiểm tra nội dung lịch học theo năm học và học kỳ đã chọn

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_26 @ui
  Scenario: Mở bản xem trước thời khóa biểu sinh viên
    When sinh viên mở bản xem trước thời khóa biểu TC_26
    Then bản xem trước TC_26 hiển thị đúng tiêu đề và chức năng in

  @TC_26 @data-verification
  Scenario: Đối chiếu dữ liệu bản xem trước với baseline
    When sinh viên mở bản xem trước thời khóa biểu TC_26
    Then dữ liệu bản xem trước TC_26 khớp baseline

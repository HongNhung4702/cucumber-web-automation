
@student @student-weekly-schedule
Feature: Kiểm thử thời khóa biểu sinh viên theo tuần
  Là sinh viên đã đăng nhập
  Tôi muốn xem thời khóa biểu theo tuần
  Để kiểm tra các buổi học theo năm học và học kỳ đã chọn

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_22 @ui
  Scenario: Hiển thị đúng thời khóa biểu tuần đã chọn
    When sinh viên mở thời khóa biểu tuần TC_22
    Then bộ lọc thời khóa biểu tuần TC_22 hiển thị đúng
    And lưới thời khóa biểu TC_22 có đúng ngày và buổi học

  @TC_22 @data-verification
  Scenario: Đối chiếu dữ liệu thời khóa biểu tuần với baseline
    When sinh viên mở thời khóa biểu tuần TC_22
    Then thời khóa biểu tuần TC_22 khớp baseline

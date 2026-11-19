
@student @exam @exam-date-range
Feature: Kiểm thử tra cứu lịch thi theo khoảng thời gian
  Là sinh viên đã đăng nhập
  Tôi muốn tra cứu lịch thi theo khoảng ngày
  Để xem các lịch thi thuộc khoảng thời gian đã chọn

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_31 @ui
  Scenario: Hiển thị đúng bộ lọc và bảng lịch thi theo thời gian
    When sinh viên tra cứu lịch thi theo khoảng ngày TC_31
    Then bộ lọc thời gian TC_31 hiển thị đúng khoảng ngày

  @TC_31 @date-range @data-verification
  Scenario: Đối chiếu danh sách lịch thi trong khoảng thời gian hợp lệ
    When sinh viên tra cứu lịch thi theo khoảng ngày TC_31
    Then lịch thi trong khoảng ngày TC_31 khớp baseline


@student @exam @exam-invalid-date-range
Feature: Kiểm thử tra cứu lịch thi với khoảng ngày không hợp lệ
  Là sinh viên đã đăng nhập
  Tôi muốn kiểm tra phản hồi khi ngày bắt đầu sau ngày kết thúc
  Để xác nhận trạng thái kết quả hiển thị theo hành vi hệ thống

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_32 @negative
  Scenario: Tra cứu lịch thi khi ngày bắt đầu lớn hơn ngày kết thúc
    When sinh viên tra cứu lịch thi với khoảng ngày không hợp lệ TC_32
    Then bộ lọc TC_32 giữ nguyên khoảng ngày bắt đầu lớn hơn ngày kết thúc
    And hai bảng lịch thi TC_32 hiển thị trạng thái không có lịch

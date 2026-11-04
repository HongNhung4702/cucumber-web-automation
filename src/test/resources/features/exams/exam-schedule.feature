
@student @exam @exam-schedule
Feature: Tra cứu lịch thi theo năm học và học kỳ
  Là sinh viên đã đăng nhập
  Tôi muốn xem lịch thi theo năm học và học kỳ
  Để theo dõi lịch thi được hệ thống cung cấp cho tài khoản của mình

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_28 @ui
  Scenario: Hiển thị lịch thi theo năm học và học kỳ đã chọn
    When sinh viên tra cứu lịch thi năm học "2022-2023" và học kỳ "Học kỳ 1"
    Then bộ lọc năm học hiển thị "2022-2023"
    And bộ lọc học kỳ hiển thị "Học kỳ 1"
    And bảng lịch thi "Tất cả" hiển thị các cột đã được xác nhận

  @TC_28 @data-verification
  Scenario: Đối chiếu danh sách lịch thi với dữ liệu tham chiếu
    When sinh viên tra cứu lịch thi năm học "2022-2023" và học kỳ "Học kỳ 1"
    Then số lượng bản ghi trong bảng "Tất cả" khớp dữ liệu tham chiếu của TC_28
    And toàn bộ bản ghi trong bảng "Tất cả" khớp dữ liệu tham chiếu của TC_28


@student @schedule @course-schedule
Feature: Xem thời khóa biểu theo lớp học phần
  Là sinh viên đã đăng nhập
  Tôi muốn tra cứu thời khóa biểu theo năm học và học kỳ
  Để theo dõi thông tin lịch học của các lớp học phần

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_21 @ui
  Scenario: Hiển thị thời khóa biểu theo năm học và học kỳ đã chọn
    When sinh viên tra cứu thời khóa biểu lớp học phần năm học "2025-2026" và học kỳ "Học kỳ 3"
    Then bộ lọc năm học của thời khóa biểu hiển thị "2025-2026"
    And bộ lọc học kỳ của thời khóa biểu hiển thị "Học kỳ 3"
    And bảng thời khóa biểu hiển thị các cột đã được xác nhận

  @TC_21 @data-verification
  Scenario: Đối chiếu danh sách thời khóa biểu với dữ liệu tham chiếu
    When sinh viên tra cứu thời khóa biểu lớp học phần năm học "2025-2026" và học kỳ "Học kỳ 3"
    Then số lượng lớp học phần khớp dữ liệu tham chiếu của TC_21
    And số lượng dòng lịch học khớp dữ liệu tham chiếu của TC_21
    And toàn bộ dữ liệu lịch học khớp dữ liệu tham chiếu của TC_21

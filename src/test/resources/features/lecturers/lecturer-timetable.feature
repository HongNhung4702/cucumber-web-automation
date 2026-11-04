
@public @timetable @lecturer-timetable
Feature: Tra cứu thời khóa biểu giảng viên
  Là người tra cứu
  Tôi muốn xem thời khóa biểu giảng viên theo năm học, học kỳ và khoa
  Để theo dõi thông tin lịch giảng dạy tương ứng với điều kiện đã chọn

  @TC_38 @ui
  Scenario: Hiển thị thời khóa biểu theo đúng điều kiện tra cứu giảng viên
    Given người tra cứu đã mở trang thời khóa biểu giảng viên
    When người tra cứu chọn năm học "2026-2027", học kỳ "Học kỳ 1", khoa "Khoa Công nghệ thông tin" và giảng viên "Phạm Trần Thiện"
    And người tra cứu nhấn Lọc thời khóa biểu giảng viên
    Then bộ lọc thời khóa biểu giảng viên hiển thị đúng điều kiện đã chọn
    And tiêu đề kết quả hiển thị khoa "Khoa Công nghệ thông tin", học kỳ "HK01" và năm học "2026-2027"
    And bảng thời khóa biểu giảng viên hiển thị đầy đủ các cột đã xác nhận

  @TC_38 @data-verification
  Scenario: Đối chiếu dữ liệu lịch giảng dạy với baseline
    Given người tra cứu đã mở trang thời khóa biểu giảng viên
    When người tra cứu chọn năm học "2026-2027", học kỳ "Học kỳ 1", khoa "Khoa Công nghệ thông tin" và giảng viên "Phạm Trần Thiện"
    And người tra cứu nhấn Lọc thời khóa biểu giảng viên
    Then số lượng lớp học phần khớp baseline của TC_38
    And số lượng dòng lịch giảng dạy khớp baseline của TC_38
    And toàn bộ dữ liệu lịch giảng dạy khớp baseline của TC_38

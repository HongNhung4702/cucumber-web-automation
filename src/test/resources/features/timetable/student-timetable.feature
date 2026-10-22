
@student @timetable @student-timetable
Feature: Xem và tra cứu thời khóa biểu sinh viên
  Là sinh viên đã đăng nhập vào Cổng thông tin đào tạo
  Tôi muốn xem thời khóa biểu theo lớp học phần, sinh viên và phòng học
  Để theo dõi lịch học theo năm học, học kỳ và tuần

  Background:
    Given sinh viên đã đăng nhập thành công vào hệ thống
    And họ tên và mã sinh viên trên giao diện khớp với tài khoản kiểm thử

  @TC_21 @smoke
  Scenario: Xem thời khóa biểu theo lớp học phần
    When sinh viên truy cập mục Thời khóa biểu
    And sinh viên chọn tab Thời khóa biểu theo lớp học phần
    And sinh viên chọn năm học "2024-2025"
    And sinh viên chọn học kỳ "Học kỳ 1"
    Then hệ thống hiển thị thời khóa biểu theo lớp học phần
    And tiêu đề thời khóa biểu hiển thị năm học "2024-2025"
    And tiêu đề thời khóa biểu hiển thị học kỳ "HK01"
    And bảng kết quả hiển thị các cột thông tin lớp học phần và lịch học
    And các lớp học phần được hiển thị theo dữ liệu của năm học và học kỳ đã chọn
    And các dòng lịch học có thông tin mã lớp học phần, tên học phần, số tín chỉ, thứ, tiết và phòng khi có dữ liệu
    And không có dữ liệu lịch học thuộc năm học hoặc học kỳ khác xuất hiện trong kết quả

  @TC_22 @personal @data-verification
  Scenario: Xem thời khóa biểu sinh viên theo tuần
    When sinh viên truy cập mục Thời khóa biểu
    And sinh viên chọn tab Thời khóa biểu sinh viên
    And sinh viên chọn năm học "2024-2025"
    And sinh viên chọn học kỳ "Học kỳ 1"
    Then hệ thống hiển thị thời khóa biểu sinh viên dạng tuần
    And các cột thứ và ngày tương ứng với tuần đang được chọn
    And các buổi học được hiển thị tại đúng thứ và ngày trên lịch
    And các buổi học có thông tin tên học phần, nhóm, lớp, tiết và giảng viên khi có dữ liệu
    And tên các học phần có lịch nhất quán với danh sách học phần đăng ký của cùng tài khoản trong cùng năm học và học kỳ

  @TC_23 @room
  Scenario: Xem thời khóa biểu theo phòng học
    When sinh viên truy cập mục Thời khóa biểu
    And sinh viên chọn tab Thời khóa biểu theo phòng
    And sinh viên chọn năm học "2024-2025"
    And sinh viên chọn học kỳ "Học kỳ 1"
    And sinh viên chọn tuần "09/12/2024-15/12/2024"
    Then hệ thống hiển thị thời khóa biểu theo phòng học
    And bộ lọc Tuần hiển thị "09/12/2024-15/12/2024"
    And các cột thứ và ngày tương ứng với tuần đã chọn
    And các phòng học được hiển thị theo từng dòng của lịch
    And các buổi học được hiển thị tại phòng, thứ và tiết tương ứng
    And các buổi học có thông tin học phần, lớp và giảng viên khi có dữ liệu
    And không có buổi học nào nằm ngoài khoảng tuần đã chọn

  @TC_24 @navigation @data-verification
  Scenario: Chuyển tuần trong thời khóa biểu sinh viên
    Given sinh viên đang ở tab Thời khóa biểu sinh viên
    And sinh viên đã chọn năm học "2024-2025"
    And sinh viên đã chọn học kỳ "Học kỳ 1"
    And sinh viên đang xem một tuần hợp lệ có thể chuyển sang tuần tiếp theo
    When sinh viên nhấn nút Tuần tiếp theo
    Then bộ lọc Tuần chuyển sang khoảng bảy ngày kế tiếp
    And các cột ngày được cập nhật theo tuần mới
    And lịch học được hiển thị tương ứng với tuần mới
    When sinh viên nhấn nút Tuần trước
    Then bộ lọc Tuần trở về khoảng ngày ban đầu
    And các cột ngày trở về tuần ban đầu
    And nội dung thời khóa biểu nhất quán với lần hiển thị ban đầu
    And các học phần hiển thị vẫn thuộc phạm vi thời khóa biểu của tài khoản sinh viên đang đăng nhập

  @TC_25 @room @navigation
  Scenario: Chuyển tuần trong thời khóa biểu theo phòng
    Given sinh viên đang ở tab Thời khóa biểu theo phòng
    And sinh viên đã chọn năm học "2024-2025"
    And sinh viên đã chọn học kỳ "Học kỳ 1"
    And sinh viên đang xem một tuần hợp lệ có thể chuyển sang tuần tiếp theo
    When sinh viên nhấn nút Tuần tiếp theo
    Then bộ lọc Tuần chuyển sang khoảng bảy ngày kế tiếp
    And các cột ngày được cập nhật theo tuần mới
    And lịch theo phòng được hiển thị tương ứng với tuần mới
    When sinh viên nhấn nút Tuần trước
    Then bộ lọc Tuần trở về khoảng ngày ban đầu
    And các cột ngày trở về tuần ban đầu
    And nội dung lịch theo phòng nhất quán với lần hiển thị ban đầu
    And các buổi học được hiển thị tại phòng, thứ và tiết tương ứng

  @TC_26 @print @data-verification
  Scenario: Xem trước và in thời khóa biểu sinh viên
    Given sinh viên đang ở tab Thời khóa biểu sinh viên
    When sinh viên chọn năm học "2022-2023"
    And sinh viên chọn học kỳ "Học kỳ 2"
    And sinh viên nhấn nút In thời khóa biểu
    Then hệ thống hiển thị hộp thoại Xem thời khóa biểu
    And bản xem trước có tiêu đề "THỜI KHÓA BIỂU SINH VIÊN"
    And bản xem trước hiển thị mã lớp học phần, tên học phần và thông tin lịch học
    And nội dung bản xem trước nhất quán với thời khóa biểu trước khi in
    When sinh viên nhấn nút In thời khóa biểu trong hộp thoại
    Then giao diện in của trình duyệt được mở
    And nội dung bản in tương ứng với bản xem trước thời khóa biểu

  @TC_27 @print @export @data-verification
  Scenario: Lưu thời khóa biểu thành tệp PDF
    Given sinh viên đang ở bản xem trước thời khóa biểu sinh viên
    When sinh viên nhấn nút In thời khóa biểu
    And sinh viên chọn đích in PDF trong trình duyệt
    And sinh viên lưu thời khóa biểu thành tệp PDF
    And sinh viên mở tệp PDF đã tạo
    Then tệp PDF được tạo thành công và có thể đọc được
    And tệp PDF hiển thị tiêu đề thời khóa biểu sinh viên
    And tệp PDF có thông tin lớp học phần và lịch học
    And nội dung tệp PDF nhất quán với bản xem trước thời khóa biểu

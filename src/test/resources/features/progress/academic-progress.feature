
@student @academic-progress
Feature: Xem và kiểm tra tiến độ học tập sinh viên
  Là sinh viên đã đăng nhập vào Cổng thông tin đào tạo
  Tôi muốn xem kết quả và tiến độ học tập của mình
  Để theo dõi tình trạng tích lũy học phần, tín chỉ và điều kiện tốt nghiệp

  Background:
    Given sinh viên đã đăng nhập thành công vào hệ thống
    And họ tên và mã sinh viên trên giao diện khớp với tài khoản kiểm thử

  @TC_15 @smoke @data-verification
  Scenario: Xem tổng quan tiến độ học tập
    When sinh viên truy cập mục Tổng quan học tập
    Then hệ thống hiển thị trang Tổng quan học tập
    And thông tin mã sinh viên, họ tên, lớp và khóa học khớp với hồ sơ tài khoản
    And hệ thống hiển thị bộ lọc Chương trình đào tạo, Năm học và Học kỳ
    And hệ thống hiển thị biểu đồ Kết quả học tập
    And hệ thống hiển thị biểu đồ Tiến độ học tập
    And hệ thống hiển thị thống kê điểm sinh viên theo thang điểm
    And số tín chỉ đã tích lũy không vượt quá tổng số tín chỉ yêu cầu
    And tỷ lệ tín chỉ tích lũy nhất quán với số tín chỉ đã tích lũy và tổng tín chỉ yêu cầu
    And dữ liệu tiến độ học tập tương ứng với tài khoản sinh viên đang đăng nhập

  @TC_16 @filter @data-verification
  Scenario: Xem tiến độ học tập theo chương trình đào tạo, năm học và học kỳ
    Given sinh viên đang ở trang Tổng quan học tập
    When sinh viên chọn chương trình đào tạo "Kỹ thuật phần mềm"
    And sinh viên chọn năm học "2025-2026"
    And sinh viên chọn học kỳ "Học kỳ 3"
    Then các bộ lọc hiển thị đúng chương trình đào tạo, năm học và học kỳ đã chọn
    And hệ thống hiển thị dữ liệu Kết quả học tập tương ứng với điều kiện đã chọn
    And hệ thống hiển thị dữ liệu Tiến độ học tập của chương trình đào tạo
    And dữ liệu học tập hiển thị thuộc tài khoản sinh viên đang đăng nhập
    And các giá trị kết quả học tập có thể đối chiếu được nhất quán với dữ liệu Xem điểm của cùng năm học và học kỳ
    And số tín chỉ tích lũy không vượt quá tổng tín chỉ yêu cầu của chương trình đào tạo

  @TC_17 @data-verification
  Scenario: Xem danh sách học phần chưa tích lũy
    Given sinh viên đang ở trang Tổng quan học tập
    And tài khoản sinh viên có học phần chưa tích lũy
    When sinh viên nhấn nút Xem học phần chưa tích lũy
    Then hộp thoại Học phần chưa tích lũy được hiển thị
    And bảng hiển thị mã học phần, tên học phần và số tín chỉ
    And các học phần được phân loại theo nhóm trong chương trình đào tạo
    And các học phần trong danh sách thuộc chương trình đào tạo của sinh viên
    And mã học phần và tên học phần nhất quán với dữ liệu Chương trình đào tạo
    And danh sách học phần chưa tích lũy tương ứng với tài khoản sinh viên đang đăng nhập

  @TC_18 @graduation @data-verification
  Scenario: Xem điều kiện xét tốt nghiệp
    Given sinh viên đang ở trang Tổng quan học tập
    When sinh viên nhấn nút Điều kiện tốt nghiệp
    Then hộp thoại Điều kiện tốt nghiệp được hiển thị
    And bảng hiển thị loại chuẩn đầu ra, điều kiện, chứng chỉ sinh viên và kết quả đánh giá
    And hệ thống hiển thị thông tin tín chỉ tích lũy và điểm trung bình tích lũy
    And hệ thống hiển thị thông tin điểm rèn luyện của sinh viên
    And hệ thống hiển thị trạng thái đánh giá của từng điều kiện tốt nghiệp
    And số tín chỉ tích lũy nhất quán với dữ liệu Tiến độ học tập của cùng tài khoản
    And kết quả đánh giá điều kiện tốt nghiệp tương ứng với hồ sơ sinh viên đang đăng nhập

  @TC_19 @graduation @data-verification
  Scenario: Xem kết quả xét tốt nghiệp
    Given sinh viên đang ở trang Tổng quan học tập
    When sinh viên nhấn nút Xem kết quả xét tốt nghiệp
    Then hộp thoại Kết quả xét tốt nghiệp được hiển thị
    And bảng có các cột Tên đợt, Đăng ký CTĐT, Ngày xét, Kết quả, ĐTB, Xếp loại và Ghi chú
    And các đợt xét tốt nghiệp hiển thị thuộc phạm vi hồ sơ sinh viên đang đăng nhập
    And chương trình đào tạo trong kết quả phù hợp với chương trình đào tạo của sinh viên
    And mỗi đợt xét hiển thị thông tin kết quả xét tốt nghiệp tương ứng
    And hệ thống hiển thị ghi chú về kết quả xét tốt nghiệp khi có dữ liệu

  @TC_20 @credits @data-verification
  Scenario: Kiểm tra tiến độ học tập theo tín chỉ
    Given sinh viên đang ở trang Tổng quan học tập
    When sinh viên nhấn nút Kiểm tra tiến độ học tập
    Then hộp thoại Kiểm tra tiến độ học tập được hiển thị
    And bảng hiển thị học kỳ theo kế hoạch đào tạo
    And bảng hiển thị số tín chỉ yêu cầu theo kế hoạch
    And bảng hiển thị số tín chỉ sinh viên tích lũy và số tín chỉ nợ đọng
    And các học kỳ trong bảng thuộc chương trình đào tạo của sinh viên
    And tổng tín chỉ yêu cầu nhất quán với tổng tín chỉ của chương trình đào tạo
    And tổng số tín chỉ sinh viên tích lũy nhất quán với dữ liệu Tiến độ học tập
    And hệ thống hiển thị ghi chú đánh giá tiến độ học tập theo dữ liệu được cung cấp

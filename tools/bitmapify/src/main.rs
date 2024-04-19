use image::io::Reader as ImageReader;

struct ImageFormat {
    header: Header,
    data: Vec<RGB>,
}

impl ImageFormat {
    pub fn to_bytes(&self) -> Vec<u8> {
        let mut buf = Vec::new();
        buf.extend_from_slice(&self.header.to_bytes());
        for rgb in &self.data {
            buf.extend_from_slice(&rgb.to_bytes());
        }
        buf
    }
}

struct Header {
    width: u32,
    height: u32,
}

impl Header {
    pub fn to_bytes(&self) -> Vec<u8> {
        let mut buf = Vec::new();
        buf.extend_from_slice(&self.width.to_be_bytes());
        buf.extend_from_slice(&self.height.to_be_bytes());
        buf
    }
}

struct RGB {
    r: u8,
    g: u8,
    b: u8,
}

impl RGB {
    fn new(r: u8, g: u8, b: u8) -> RGB {
        RGB { r, g, b }
    }

    pub fn to_bytes(&self) -> Vec<u8> {
        vec![self.r, self.g, self.b]
    }
}

fn main() {
    // The first arg is the path to the image
    let path = std::env::args().nth(1).unwrap();
    // The second arg is the output file
    let output = std::env::args().nth(2).unwrap();

    let img = ImageReader::open(path).unwrap().decode().unwrap();
    let img = img.to_rgb8();
    let (width, height) = img.dimensions();
    let mut data = Vec::new();
    for y in 0..height {
        for x in 0..width {
            let pixel = img.get_pixel(x, y);
            data.push(RGB::new(pixel[0], pixel[1], pixel[2]));
        }
    }
    let header = Header {
        width: width,
        height: height,
    };
    let img = ImageFormat { header, data };
    let img = img.to_bytes();
    // Write to file
    std::fs::write(output, img).unwrap();
}

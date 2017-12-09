//
//  ViewController.swift
//  TrackNMe
//
//  Created by Victor Hugo Martins Lisboa on 05/12/2017.
//  Copyright © 2017 Victor Hugo Martins Lisboa. All rights reserved.
//

import UIKit
import GoogleMaps
import GooglePlaces

class ViewController: UIViewController, CLLocationManagerDelegate {

    var rectangle = GMSPolyline()
    
    var mapView: GMSMapView?
    
    var locat = [[String:AnyObject]]()
    
    var locationManager = CLLocationManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        getCache()
        
        locationManager = CLLocationManager()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestAlwaysAuthorization()
        locationManager.distanceFilter = 50
        locationManager.startUpdatingLocation()
        locationManager.delegate = self
        
        let camera = GMSCameraPosition.camera(withLatitude: (locationManager.location?.coordinate.latitude)!, longitude: (locationManager.location?.coordinate.longitude)!, zoom: 16)
        mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        mapView?.isMyLocationEnabled = true
        mapView?.settings.myLocationButton = true
        mapView?.settings.compassButton = true
        mapView?.settings.zoomGestures = true
        view = mapView
        
        
        
        let currentLocation = CLLocationCoordinate2DMake((locationManager.location?.coordinate.latitude)!, (locationManager.location?.coordinate.latitude)!)
        let marker = GMSMarker(position: currentLocation)
        
        
        let date = Date()
        let formatter = DateFormatter()
        
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        let finalDate = formatter.string(from: date)
        
        marker.title = finalDate
        
        marker.map = mapView
        marker.icon = #imageLiteral(resourceName: "pin")
        marker.appearAnimation = .pop

        _ = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(rotas), userInfo: nil, repeats: true)
    }

    func saveToJsonFile() {
        
        guard let documentDirectoryUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else { return }
        let fileUrl = documentDirectoryUrl.appendingPathComponent("posicoes.json")
        
        do {
            let data = try JSONSerialization.data(withJSONObject: locat, options: .prettyPrinted)
            
            //            print(fileUrl)
            
            let jsonString = NSString(data: data, encoding: String.Encoding.utf8.rawValue)! as String
            
            try jsonString.write(to: fileUrl, atomically: true, encoding: String.Encoding(rawValue: 1))
            
        } catch {
            print(error)
        }
        
    }
    
    func getCache() {
        guard let documentDirectoryUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else { return }
        let fileUrl = documentDirectoryUrl.appendingPathComponent("posicoes.json").path
        
        let fileManager = FileManager.default
        
        if fileManager.fileExists(atPath: fileUrl) {
            do {
                let data = try Data(contentsOf: URL(fileURLWithPath: fileUrl), options: .mappedIfSafe)

                let jsonResult = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as! [[String: AnyObject]]

                locat = jsonResult

            } catch {

            }
        }
        else {
            
            let urlSt = "https://private-20ed9-tracknme.apiary-mock.com"
            let url = URL(string: urlSt)
            
            let task = URLSession.shared.dataTask(with: url!, completionHandler: { (data, response, error) in
                do {
                    
                    let jsonResult = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [[String: AnyObject]]
                    self.locat = jsonResult
                    self.saveToJsonFile()
                }
                    
                catch {
                    
                }
                
            })
            task.resume()
        }
    }
    
    @objc func rotas() {
        
        var arrWay = [String]()
        
        arrWay.removeAll()
        
        for i in (1..<9).reversed() {
            if i == 8 {
                arrWay.append("&waypoints=via:\(locat[i]["latitude"] as AnyObject)%2C\(locat[i]["longitude"] as AnyObject)")
            }
            else {
                arrWay.append("%7Cvia:\(locat[i]["latitude"] as AnyObject)%2C\(locat[i]["longitude"] as AnyObject)")
            }
        }
        
        let url = "https://maps.googleapis.com/maps/api/directions/json?origin=\(locat[9]["latitude"] as AnyObject),\(locat[9]["longitude"] as AnyObject)\(arrWay.joined(separator: ""))&destination=\(locationManager.location?.coordinate.latitude as AnyObject),\(locationManager.location?.coordinate.longitude as AnyObject)&mode=driving"
        
        
        let urlRequest = URLRequest(url: URL(string: url)!)
        
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        
        let task = session.dataTask(with: urlRequest) { (data, response, error) in
            do {
                guard let data = data else {
                    return
                }
                
                guard let json = try JSONSerialization.jsonObject(with: data, options: []) as? NSDictionary else {
                    return
                }
                
                DispatchQueue.global(qos: .background).async {
                    let array = json["routes"] as! NSArray
                    let dic = array[0] as! NSDictionary
                    let dic1 = dic["overview_polyline"] as! NSDictionary
                    let points = dic1["points"] as! String
                    print(points)
                    
                    DispatchQueue.main.async {
                        let path = GMSPath(fromEncodedPath: points)
                        self.rectangle.map = nil
                        self.rectangle = GMSPolyline(path: path)
                        self.rectangle.strokeWidth = 4
                        self.rectangle.strokeColor = UIColor(red: 72/255, green: 153/255, blue: 252/255, alpha: 1)
                        self.rectangle.map = self.mapView
                    }
                }
            }
            catch {
                
            }
        }
        task.resume()
        
        
        for i in (1...9).reversed() {
            let currentLocation = CLLocationCoordinate2DMake(locat[i]["latitude"] as! CLLocationDegrees, locat[i]["longitude"] as! CLLocationDegrees)
            let marker = GMSMarker(position: currentLocation)
            
            marker.title = locat[i]["dateTime"] as? String
            marker.map = mapView
            marker.icon = #imageLiteral(resourceName: "pin")
            marker.appearAnimation = .pop
        }
        
        let date = Date()
        let formatter = DateFormatter()
        
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        let finalDate = formatter.string(from: date)
        
        if (locat[0]["latitude"] as? CLLocationDegrees != locationManager.location?.coordinate.latitude) || (locat[0]["longitude"] as! CLLocationDegrees != locationManager.location?.coordinate.longitude) {
            locat.insert(["latitude": locationManager.location?.coordinate.latitude as AnyObject, "longitude": locationManager.location?.coordinate.longitude as AnyObject, "dateTime": finalDate as AnyObject], at: 0)
        }
        saveToJsonFile()
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}


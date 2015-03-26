module LastFmImporter 

open System
open FSharp.Data
open HttpClient

// Build our types based on samples of the JSON we expect.
type LastFmApiCredentials = JsonProvider<"data/lastfm/credentials.sample.json">
type LastFmTracks = JsonProvider<"data/lastfm/user.getTopTracks.json">
type LastFmUser = JsonProvider<"data/lastfm/user.getInfo.json">

// You need to provide this file with a valid API key for this to work
let loadApiKey () =
    LastFmApiCredentials.Load(
        __SOURCE_DIRECTORY__ + "/data/lastfm/credentials.json").ApiKey

type User(name: string, trackCount: int) =
    member this.Name = name
    member this.TrackCount = trackCount

type Artist(name: string, 
            mbid: Guid option) = class end
type Track(name: string,
           artist: Artist, 
           mbid: Guid option) = class end

let baseUri = "http://ws.audioscrobbler.com/2.0/"


let buildUserInfoRequest(username: string, apiKey: string) =
    createRequest Get baseUri
    |> withQueryStringItem { name="api_key"; value=apiKey }  
    |> withQueryStringItem { name="format"; value="json" }  
    |> withQueryStringItem { name="method"; value="user.getInfo" }  
    |> withQueryStringItem { name="user"; value=username }  

// Gets some user information from the Last.fm API
let getUser(username: string):Async<User> = async {
    let apiKey = loadApiKey ()
    let request = buildUserInfoRequest(username, apiKey)
    let! response = (request |> getResponseBodyAsync)
    let json = LastFmUser.Parse(response)
    return User(json.User.Name, json.User.Playcount)
}